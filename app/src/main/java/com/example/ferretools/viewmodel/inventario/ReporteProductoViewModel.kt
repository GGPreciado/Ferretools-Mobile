package com.example.ferretools.viewmodel.inventario

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Compra
import com.example.ferretools.model.database.Venta
import com.example.ferretools.model.states.inventario.ReporteProductoUiState
import com.example.ferretools.repository.UsuarioRepository
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import kotlin.collections.set

class ReporteProductoViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ReporteProductoUiState())
    val uiState = _uiState.asStateFlow()

    val db = Firebase.firestore

    fun cargarComprasDeProducto(productoId: String) {
        Log.e("DEBUG", "ProductoId: $productoId")
        val negocioId = SesionUsuario.usuario?.negocioId

        if (negocioId != null) {
            db.collection("compras")
                .whereEqualTo("negocioId", negocioId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val compras = snapshot.documents.mapNotNull { it.toObject(Compra::class.java) }
                    Log.e("DEBUG", "Compras sin filtrar: $compras")
                    val comprasDelProducto = compras.filter { compra ->
                        compra.lista_productos.any { it.producto_id == productoId }
                    }

                    _uiState.value = _uiState.value.copy(compras = comprasDelProducto)

                    // Actualizar los componentes dependientes de las compras
                    agruparUnidadesCompraPorPeriodo(productoId = productoId)

                    viewModelScope.launch {
                        actualizarIndicadores()
                    }

                    Log.e("DEBUG", "Compras: ${_uiState.value.compras}")
                }
                .addOnFailureListener { e ->
                    Log.e("DEBUG", "Error: ${e.message}")
                }
        } else {
            Log.e("DEBUG", "Sesión no iniciada")
        }
    }

    fun cargarVentasDeProducto(productoId: String) {
        Log.e("DEBUG", "ProductoId: $productoId")
        val negocioId = SesionUsuario.usuario?.negocioId

        if (negocioId != null) {
            db.collection("ventas")
                .whereEqualTo("negocioId", negocioId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val ventas = snapshot.documents.mapNotNull { it.toObject(Venta::class.java) }
                    Log.e("DEBUG", "Ventas sin filtrar: $ventas")
                    val ventasDelProducto = ventas.filter { venta ->
                        venta.lista_productos.any { it.producto_id == productoId }
                    }

                    _uiState.value = _uiState.value.copy(ventas = ventasDelProducto)

                    // Actualizar los componentes dependientes de las ventas
                    agruparUnidadesVentaPorPeriodo(productoId = productoId)

                    viewModelScope.launch {
                        actualizarIndicadores()
                    }

                    Log.e("DEBUG", "Ventas: ${_uiState.value.ventas}")
                }
                .addOnFailureListener { e ->
                    Log.e("DEBUG", "Error: ${e.message}")
                }
        } else {
            Log.e("DEBUG", "Sesión no iniciada")
        }
    }

    fun cambiarOperacionSeleccionada(operacion: String) {
        _uiState.value = _uiState.value.copy(operacionSeleccionada = operacion)
    }

    fun cambiarValoresGrafico(valores: List<Float>) {
        _uiState.update { it.copy(valoresGrafico = valores) }
    }

    fun cambiarFechasGrafico(fechas: List<LocalDate>) {
        _uiState.update { it.copy(fechasGrafico = fechas) }
    }

    // KPIs
    private fun cambiarUnidadesVendidas(unidades: Int) {
        _uiState.update { it.copy(unidadesVendidas = unidades) }
    }

    private fun cambiarUnidadesCompradas(unidades: Int) {
        _uiState.update { it.copy(unidadesCompradas = unidades) }
    }

    private fun cambiarTotalRecaudadoVentas(total: Double) {
        _uiState.update { it.copy(totalRecaudadoVentas = total) }
    }

    private fun cambiarTotalInvertidoCompras(total: Double) {
        _uiState.update { it.copy(totalInvertidoCompras = total) }
    }

    private fun cambiarGananciaPromedioVenta(ganancia: Double) {
        _uiState.update { it.copy(gananciaPromedioVenta = ganancia) }
    }

    private fun cambiarPrecioPromedioCompra(precio: Double) {
        _uiState.update { it.copy(precioPromedioCompra = precio) }
    }

    private fun cambiarUsuarioMayoresVentas(usuario: String) {
        _uiState.update { it.copy(usuarioMayoresVentas = usuario) }
    }

    private fun cambiarUsuarioMayoresCompras(usuario: String) {
        _uiState.update { it.copy(usuarioMayoresCompras = usuario) }
    }

//    fun agruparUnidadesPorDiaUltimaSemana(productoId: String): List<Int> {
//        val ventas = _uiState.value.ventas!!
//        val hoy = LocalDate.now()
//        val dias = (0..6).map { hoy.minusDays(it.toLong()) }.reversed()
//
//        val mapa = dias.associateWith { 0 }.toMutableMap()
//
//        for (venta in ventas) {
//            val fechaVenta = venta.fecha?.toDate()!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
//            if (fechaVenta in mapa.keys) {
//                val unidades = venta.lista_productos
//                    .filter { it.producto_id == productoId }
//                    .sumOf { it.cantidad!! }
//                mapa[fechaVenta] = mapa[fechaVenta]!! + unidades
//            }
//        }
//
//        return mapa.values.toList()
//    }

    private fun agruparUnidadesVentaPorPeriodo(productoId: String){

        val ventas = _uiState.value.ventas
        val periodo: String = _uiState.value.periodoTemporal
        val now = LocalDate.now()

        if (ventas != null) {
            when (periodo) {
                "Diario" -> {
                    val dias = (0..6).map { now.minusDays(it.toLong()) }.reversed()
                    val mapa = dias.associateWith { 0 }.toMutableMap()

                    ventas.forEach { item ->
                        val fecha = item.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                        if (fecha in mapa.keys) {
                            val unidades = item.lista_productos.filter { it.producto_id == productoId }
                                .sumOf { it.cantidad ?: 0 }
                            mapa[fecha!!] = mapa[fecha]!! + unidades
                        }
                    }
                    cambiarValoresGrafico(mapa.values.map { it.toFloat() })
                    cambiarFechasGrafico(dias)
                }

                "Semanal" -> {
                    val semanas = (0..6).map { now.minusWeeks(it.toLong()).with(DayOfWeek.MONDAY) }.reversed()
                    val mapa = semanas.associateWith { 0 }.toMutableMap()

                    ventas.forEach { item ->
                        val fecha = item.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                        val semana = fecha?.with(DayOfWeek.MONDAY)
                        if (semana in mapa.keys) {
                            val unidades = item.lista_productos.filter { it.producto_id == productoId }
                                .sumOf { it.cantidad ?: 0 }
                            mapa[semana!!] = mapa[semana]!! + unidades
                        }
                    }

                    cambiarValoresGrafico(mapa.values.map { it.toFloat() })
                    cambiarFechasGrafico(semanas)
//                    mapa.values.map { it.toFloat() } to semanas
                }

                "Mensual" -> {
                    val meses = (0..6).map { now.minusMonths(it.toLong()).withDayOfMonth(1) }.reversed()
                    val mapa = meses.associateWith { 0 }.toMutableMap()

                    ventas.forEach { item ->
                        val fecha = item.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                        val mes = fecha?.withDayOfMonth(1)
                        if (mes in mapa.keys) {
                            val unidades = item.lista_productos.filter { it.producto_id == productoId }
                                .sumOf { it.cantidad ?: 0 }
                            mapa[mes!!] = mapa[mes]!! + unidades
                        }
                    }
                    cambiarValoresGrafico(mapa.values.map { it.toFloat() })
                    cambiarFechasGrafico(meses)
//                    mapa.values.map { it.toFloat() } to meses
                }

                else -> {
                    Log.e("DEBUG", "Elija una opción válida")
                }
            }
        }
        else {
            Log.e("DEBUG", "La lista de ventas es null")
        }

    }

    private fun agruparUnidadesCompraPorPeriodo(productoId: String) {

        val compras = _uiState.value.compras
        val periodo: String = _uiState.value.periodoTemporal
        val now = LocalDate.now()

        if (compras != null) {
            when (periodo) {
                "Diario" -> {
                    val dias = (0..6).map { now.minusDays(it.toLong()) }.reversed()
                    val mapa = dias.associateWith { 0 }.toMutableMap()

                    compras.forEach { item ->
                        val fecha = item.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                        if (fecha in mapa.keys) {
                            val unidades = item.lista_productos.filter { it.producto_id == productoId }
                                .sumOf { it.cantidad ?: 0 }
                            mapa[fecha!!] = mapa[fecha]!! + unidades
                        }
                    }

                    cambiarValoresGrafico(mapa.values.map { it.toFloat() })
                    cambiarFechasGrafico(dias)
//                    .map { it.format(DateTimeFormatter.ofPattern("dd MMM")) }
                }

                "Semanal" -> {
                    val semanas = (0..6).map { now.minusWeeks(it.toLong()).with(DayOfWeek.MONDAY) }.reversed()
                    val mapa = semanas.associateWith { 0 }.toMutableMap()

                    compras.forEach { item ->
                        val fecha = item.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                        val semana = fecha?.with(DayOfWeek.MONDAY)
                        if (semana in mapa.keys) {
                            val unidades = item.lista_productos.filter { it.producto_id == productoId }
                                .sumOf { it.cantidad ?: 0 }
                            mapa[semana!!] = mapa[semana]!! + unidades
                        }
                    }

                    cambiarValoresGrafico(mapa.values.map { it.toFloat() })
                    cambiarFechasGrafico(semanas)
//                    .map {
//                    "Sem. ${it.get(ChronoField.ALIGNED_WEEK_OF_YEAR)}"
//                }
                }

                "Mensual" -> {
                    val meses = (0..6).map { now.minusMonths(it.toLong()).withDayOfMonth(1) }.reversed()
                    val mapa = meses.associateWith { 0 }.toMutableMap()

                    compras.forEach { item ->
                        val fecha = item.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                        val mes = fecha?.withDayOfMonth(1)
                        if (mes in mapa.keys) {
                            val unidades = item.lista_productos.filter { it.producto_id == productoId }
                                .sumOf { it.cantidad ?: 0 }
                            mapa[mes!!] = mapa[mes]!! + unidades
                        }
                    }

                    cambiarValoresGrafico(mapa.values.map { it.toFloat() })
                    cambiarFechasGrafico(meses)
//                    .map { it.format(DateTimeFormatter.ofPattern("MMM yyyy")) }
                }

                else -> {
                    Log.e("DEBUG", "Elija una opción válida")
                }
            }
        }
        else {
            Log.e("DEBUG", "La lista de compras es null")
        }

    }

    suspend fun actualizarIndicadores() {
        if (_uiState.value.operacionSeleccionada == "Ventas") {
            cambiarUnidadesVendidas(
                contarUnidadesVendidas()
            )
            cambiarTotalRecaudadoVentas(
                calcularTotalRecaudadoVentas()
            )
            cambiarGananciaPromedioVenta(
                calcularGananciaPromedioVentas()
            )
            cambiarUsuarioMayoresVentas(
                calcularUsuarioMayorVentas()
            )

        } else if (_uiState.value.operacionSeleccionada == "Compras") {
            cambiarUnidadesCompradas(
                contarUnidadesCompradas()
            )
            cambiarTotalInvertidoCompras(
                calcularTotalInvertidoCompras()
            )
            cambiarPrecioPromedioCompra(
                calcularPrecioPromedioCompra()
            )
            cambiarUsuarioMayoresCompras(
                calcularUsuarioMayorCompras()
            )
        }
    }

    fun contarUnidadesVendidas(): Int {
        val listaVentas = _uiState.value.ventas
        var unidadesVendidas = 0
        if (listaVentas != null) {
            listaVentas.forEach { item ->
                val cantidadVentaUnitaria = item.lista_productos
                    .sumOf { it.cantidad ?: 0 }
                unidadesVendidas += cantidadVentaUnitaria
            }
            return unidadesVendidas
        } else {
            return 0
        }
    }

    fun contarUnidadesCompradas(): Int {
        val listaCompras = _uiState.value.compras
        var unidadesCompradas = 0
        if (listaCompras != null) {
            listaCompras.forEach { item ->
                val cantidadCompraUnitaria = item.lista_productos
                    .sumOf { it.cantidad ?: 0 }
                unidadesCompradas += cantidadCompraUnitaria
            }
            return unidadesCompradas
        } else {
            return 0
        }
    }

    fun calcularTotalRecaudadoVentas(): Double {
        val listaVentas = _uiState.value.ventas
        var totalRecaudado = 0.0
        if (listaVentas != null) {
            listaVentas.forEach { item ->
                val totalVentaUnitario = item.lista_productos
                    .sumOf { it.subtotal ?: 0.0 }
                totalRecaudado += totalVentaUnitario
            }
            return totalRecaudado
        } else {
            return 0.0
        }
    }

    fun calcularTotalInvertidoCompras(): Double {
        val listaCompras = _uiState.value.compras
        var totalRecaudado = 0.0
        if (listaCompras != null) {
            listaCompras.forEach { item ->
                val totalCompraUnitario = item.lista_productos
                    .sumOf { it.subtotal ?: 0.0 }
                totalRecaudado += totalCompraUnitario
            }
            return totalRecaudado
        } else {
            return 0.0
        }
    }

    fun calcularGananciaPromedioVentas(): Double {
        val listaVentas = _uiState.value.ventas
        if (listaVentas != null) {
            val totalRecaudado = calcularTotalRecaudadoVentas()
            val cantidadVentas = listaVentas.size
            return totalRecaudado / cantidadVentas
        } else {
            return 0.0
        }
    }

    fun calcularPrecioPromedioCompra(): Double {
        val listaCompras = _uiState.value.compras
        if (listaCompras != null) {
            val totalInvertido = calcularTotalInvertidoCompras()
            val cantidadUnidadesCompradas = contarUnidadesCompradas()
            return totalInvertido / cantidadUnidadesCompradas
        } else {
            return 0.0
        }
    }

    suspend fun calcularUsuarioMayorVentas(): String {
        val listaVentas = _uiState.value.ventas ?: return "Error"

        val conteoPorUsuario = mutableMapOf<String, Int>()

        listaVentas.forEach { venta ->
            val usuarioId = venta.atendedor_id ?: return@forEach
            // Accede al value de usuarioId, crea el key si no existe
            conteoPorUsuario[usuarioId] = conteoPorUsuario.getOrDefault(usuarioId, 0) + 1
        }

        var usuarioConMasVentasId = ""
        var maxVentas = 0

        conteoPorUsuario.forEach { (usuarioId, cantidad) ->
            if (cantidad > maxVentas) {
                maxVentas = cantidad
                usuarioConMasVentasId = usuarioId
            }
        }

        // Obtener el nombre del usuario
        val nombreUsuario = UsuarioRepository(db).obtenerUsuarioPorId(usuarioConMasVentasId)

        return nombreUsuario?.nombre ?: "Error"
    }

    suspend fun calcularUsuarioMayorCompras(): String {
        val listaCompras = _uiState.value.ventas ?: return "Error"

        val conteoPorUsuario = mutableMapOf<String, Int>()

        listaCompras.forEach { compra ->
            val usuarioId = compra.atendedor_id ?: return@forEach
            // Accede al value de usuarioId, crea el key si no existe
            conteoPorUsuario[usuarioId] = conteoPorUsuario.getOrDefault(usuarioId, 0) + 1
        }

        var usuarioConMasComprasId = ""
        var maxCompras = 0

        conteoPorUsuario.forEach { (usuarioId, cantidad) ->
            if (cantidad > maxCompras) {
                maxCompras = cantidad
                usuarioConMasComprasId = usuarioId
            }
        }

        // Obtener el nombre del usuario
        val nombreUsuario = UsuarioRepository(db).obtenerUsuarioPorId(usuarioConMasComprasId)

        return nombreUsuario?.nombre ?: "Error"
    }

}