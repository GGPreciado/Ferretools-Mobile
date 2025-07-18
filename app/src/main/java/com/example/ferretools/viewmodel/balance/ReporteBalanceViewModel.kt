package com.example.ferretools.viewmodel.balance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Compra
import com.example.ferretools.model.database.Venta
import com.example.ferretools.model.states.balance.ReporteBalanceUiState
import com.example.ferretools.repository.ProductoRepository
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

class ReporteBalanceViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ReporteBalanceUiState())
    val uiState = _uiState.asStateFlow()

    private val db = Firebase.firestore

    private val productoRepository = ProductoRepository()

    fun cargarVentasDeProducto() {
        val negocioId = SesionUsuario.usuario?.negocioId

        if (negocioId != null) {
            db.collection("ventas")
                .whereEqualTo("negocioId", negocioId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val ventas = snapshot.documents.mapNotNull { it.toObject(Venta::class.java) }
                    Log.e("DEBUG", "Ventas sin filtrar: $ventas")

                    _uiState.value = _uiState.value.copy(listaVentas = ventas)

                    // Actualizar los componentes dependientes de las ventas
                    when (_uiState.value.tipoGrafico) {
                        "Barras" -> {
                            actualizarGraficoBarrasVentas()
                        }
                        "Apiladas" -> {
                            Log.d("DEBUG", "Actualizar barras apiladas")
                            viewModelScope.launch {
                                actualizarGraficoBarrasApiladasVentas()
                            }
                        }
                    }

                    viewModelScope.launch {
                        actualizarIndicadores()
                    }

                    Log.e("DEBUG", "Ventas: ${_uiState.value.listaVentas}")
                }
                .addOnFailureListener { e ->
                    Log.e("DEBUG", "Error: ${e.message}")
                }
        } else {
            Log.e("DEBUG", "Sesión no iniciada")
        }
    }

    fun cargarComprasDeProducto() {
        val negocioId = SesionUsuario.usuario?.negocioId

        if (negocioId != null) {
            db.collection("compras")
                .whereEqualTo("negocioId", negocioId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val compras = snapshot.documents.mapNotNull { it.toObject(Compra::class.java) }
                    Log.e("DEBUG", "Compras sin filtrar: $compras")

                    _uiState.value = _uiState.value.copy(listaCompras = compras)

                    // Actualizar los componentes dependientes de las compras
                    when (_uiState.value.tipoGrafico) {
                        "Barras" -> {
                            actualizarGraficoBarrasCompras()
                        }
                        "Apiladas" -> {
                            Log.d("DEBUG", "Actualizar barras apiladas")
                            viewModelScope.launch {
                                actualizarGraficoBarrasApiladasCompras()
                            }
                        }
                    }

                    viewModelScope.launch {
                        actualizarIndicadores()
                    }

                    Log.e("DEBUG", "Compras: ${_uiState.value.listaCompras}")
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

    // Actualizar variables gráficos

    fun cambiarTipoGrafico(tipo: String) {
        _uiState.update { it.copy(tipoGrafico = tipo) }
        elegirActualizarGrafico()
    }

    fun cambiarPeriodoTemporal(periodo: String) {
        _uiState.update { it.copy(periodoTemporal = periodo) }
        elegirActualizarGrafico()
    }

    fun cambiarValoresGrafico(valores: List<Float>) {
        _uiState.update { it.copy(datosGrafico = valores) }
    }

    fun cambiarFechasGrafico(fechas: List<LocalDate>) {
        _uiState.update { it.copy(fechasGrafico = fechas) }
    }

    // Actualizar variables KPIs
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

    private fun cambiarCantidadCategoriasVentas(cantidad: Int) {
        _uiState.update { it.copy(cantidadCategoriasUnicasVentas = cantidad) }
    }

    private fun cambiarCantidadCategoriasCompras(cantidad: Int) {
        _uiState.update { it.copy(cantidadCategoriasUnicasCompras = cantidad) }
    }

    private fun cambiarCantidadProductosUnicosVendidos(cantidad: Int) {
        _uiState.update { it.copy(cantidadProductosUnicosVendidos = cantidad) }
    }

    private fun cambiarCantidadProductosUnicosComprados(cantidad: Int) {
        _uiState.update { it.copy(cantidadProductosUnicosComprados = cantidad) }
    }

    // Gráficos
    fun elegirActualizarGrafico() {
        if (_uiState.value.operacionSeleccionada == "Ventas") {
            cargarVentasDeProducto()
        } else {
            cargarComprasDeProducto()
        }
    }

    // Gráfico de Barras
    private fun actualizarGraficoBarrasVentas() {

        val ventas = _uiState.value.listaVentas ?: return
        val periodo: String = _uiState.value.periodoTemporal
        val now = LocalDate.now()

        when (periodo) {
            "Diario" -> {
                // Devuelve una lista de LocalDate con los días restados
                val dias = (0..6).map { now.minusDays(it.toLong()) }.reversed()
                // Devuelve un mapa de la forma día: 0
                val mapa = dias.associateWith { 0 }.toMutableMap()

                ventas.forEach { item ->
                    val fecha = item.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    if (fecha in mapa.keys) {
                        val unidades = item.lista_productos
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
                        val unidades = item.lista_productos
                            .sumOf { it.cantidad ?: 0 }
                        mapa[semana!!] = mapa[semana]!! + unidades
                    }
                }

                cambiarValoresGrafico(mapa.values.map { it.toFloat() })
                cambiarFechasGrafico(semanas)
            }

            "Mensual" -> {
                // Genera los últimos 5 periodos de 1 mes, del más antiguo al más reciente
                val periodos = (4 downTo 0).map { offset ->
                    val inicio = now.minusMonths(offset.toLong())
                    val fin = inicio.plusMonths(1)
                    inicio to fin
                }

                // Mapa para agrupar unidades por periodo
                val mapa = periodos.associateWith { 0 }.toMutableMap()

                ventas.forEach { item ->
                    val fechaItem = item.fecha
                        ?.toDate()
                        ?.toInstant()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toLocalDate()

                    if (fechaItem != null) {
                        val periodo = periodos.find { fechaItem >= it.first && fechaItem < it.second }
                        if (periodo != null) {
                            val unidades = item.lista_productos.sumOf { it.cantidad ?: 0 }
                            mapa[periodo] = mapa[periodo]!! + unidades
                        }
                    }
                }

                // Mostrar los datos agrupados
                val valores = mapa.values.map { it.toFloat() }

                // Obtener una lista de LocalDates con el primer día de cada mes para las etiquetas
                val etiquetas = periodos.map { it.first }

                // Usar en tu gráfico
                cambiarValoresGrafico(valores)
                cambiarFechasGrafico(etiquetas)
            }
            else -> {
                Log.e("DEBUG", "Elija una opción válida")
            }
        }

    }

    private fun actualizarGraficoBarrasCompras() {

        val compras = _uiState.value.listaCompras?: return
        val periodo: String = _uiState.value.periodoTemporal
        val now = LocalDate.now()

        when (periodo) {
            "Diario" -> {
                // Devuelve una lista de LocalDate con los días restados
                val dias = (0..6).map { now.minusDays(it.toLong()) }.reversed()
                // Devuelve un mapa de la forma día: 0
                val mapa = dias.associateWith { 0 }.toMutableMap()

                compras.forEach { item ->
                    val fecha = item.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    if (fecha in mapa.keys) {
                        val unidades = item.lista_productos
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

                compras.forEach { item ->
                    val fecha = item.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    val semana = fecha?.with(DayOfWeek.MONDAY)
                    if (semana in mapa.keys) {
                        val unidades = item.lista_productos
                            .sumOf { it.cantidad ?: 0 }
                        mapa[semana!!] = mapa[semana]!! + unidades
                    }
                }

                cambiarValoresGrafico(mapa.values.map { it.toFloat() })
                cambiarFechasGrafico(semanas)
            }

            "Mensual" -> {
                // Genera los últimos 5 periodos de 1 mes, del más antiguo al más reciente
                val periodos = (4 downTo 0).map { offset ->
                    val inicio = now.minusMonths(offset.toLong())
                    val fin = inicio.plusMonths(1)
                    inicio to fin
                }

                // Mapa para agrupar unidades por periodo
                val mapa = periodos.associateWith { 0 }.toMutableMap()

                compras.forEach { item ->
                    val fechaItem = item.fecha
                        ?.toDate()
                        ?.toInstant()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toLocalDate()

                    if (fechaItem != null) {
                        val periodo = periodos.find { fechaItem >= it.first && fechaItem < it.second }
                        if (periodo != null) {
                            val unidades = item.lista_productos.sumOf { it.cantidad ?: 0 }
                            mapa[periodo] = mapa[periodo]!! + unidades
                        }
                    }
                }

                // Mostrar los datos agrupados
                val valores = mapa.values.map { it.toFloat() }

                // Obtener una lista de LocalDates con el primer día de cada mes para las etiquetas
                val etiquetas = periodos.map { it.first }

                // Usar en tu gráfico
                cambiarValoresGrafico(valores)
                cambiarFechasGrafico(etiquetas)
            }
            else -> {
                Log.e("DEBUG", "Elija una opción válida")
            }
        }

    }

    // Gráfico de barras apiladas

    suspend fun actualizarGraficoBarrasApiladasVentas() {
        val ventas = _uiState.value.listaVentas ?: return
        val periodo = _uiState.value.periodoTemporal
        val now = LocalDate.now()

        val fechas: List<LocalDate> = when (periodo) {
            "Diario" -> (0..6).map { now.minusDays(it.toLong()) }.reversed()
            "Semanal" -> (0..6).map { now.minusWeeks(it.toLong()).with(DayOfWeek.MONDAY) }.reversed()
            "Mensual" -> (4 downTo 0).map { now.minusMonths(it.toLong()).withDayOfMonth(1) }
            else -> return
        }

        val mapaPorFechaYUsuario = fechas.associateWith { mutableMapOf<String, Int>() }.toMutableMap()

        val atendedorIds = mutableSetOf<String>()

        ventas.forEach { venta ->
            val fechaVenta = venta.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            val atendedorId = venta.atendedor_id ?: "Desconocido"
            if (atendedorId != "Desconocido") atendedorIds.add(atendedorId)

            val claveFecha = when (periodo) {
                "Diario" -> fechaVenta
                "Semanal" -> fechaVenta?.with(DayOfWeek.MONDAY)
                "Mensual" -> fechaVenta?.withDayOfMonth(1)
                else -> null
            }

            if (claveFecha != null && claveFecha in mapaPorFechaYUsuario) {
                val cantidad = venta.lista_productos.sumOf { it.cantidad ?: 0 }
                val usuarioMap = mapaPorFechaYUsuario[claveFecha]!!
                usuarioMap[atendedorId] = usuarioMap.getOrDefault(atendedorId, 0) + cantidad
            }
        }

        // Obtener nombres reales por cada atendedorId
        val repositorio = UsuarioRepository(db)
        val mapaIdANombre: Map<String, String> = atendedorIds.associateWith { id ->
            repositorio.obtenerUsuarioPorId(id)?.nombre ?: "Usuario $id"
        }

        // Reemplazar los IDs por nombres reales
        val mapaFinalPorFechaYNombre = mapaPorFechaYUsuario.mapValues { (_, mapaPorId) ->
            mapaPorId.mapKeys { (id, _) ->
                if (id == "Desconocido") "Desconocido" else mapaIdANombre[id] ?: id
            }.toMutableMap()
        }

        val nombresUsuarios = mapaFinalPorFechaYNombre.values.flatMap { it.keys }.toSet()

        val datosPorUsuario = nombresUsuarios.associateWith { nombre ->
            fechas.map { fecha -> mapaFinalPorFechaYNombre[fecha]?.get(nombre)?.toFloat() ?: 0f }
        }

        _uiState.update {
            it.copy(
                fechasGrafico = fechas,
                datosGraficoPorUsuario = datosPorUsuario
            )
        }
    }

    suspend fun actualizarGraficoBarrasApiladasCompras() {
        val compras = _uiState.value.listaCompras ?: return
        val periodo = _uiState.value.periodoTemporal
        val now = LocalDate.now()

        val fechas: List<LocalDate> = when (periodo) {
            "Diario" -> (0..6).map { now.minusDays(it.toLong()) }.reversed()
            "Semanal" -> (0..6).map { now.minusWeeks(it.toLong()).with(DayOfWeek.MONDAY) }.reversed()
            "Mensual" -> (4 downTo 0).map { now.minusMonths(it.toLong()).withDayOfMonth(1) }
            else -> return
        }

        val mapaPorFechaYUsuario = fechas.associateWith { mutableMapOf<String, Int>() }.toMutableMap()

        val atendedorIds = mutableSetOf<String>()

        compras.forEach { compra ->
            val fechaCompra = compra.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            val atendedorId = compra.usuarioId ?: "Desconocido"
            if (atendedorId != "Desconocido") atendedorIds.add(atendedorId)

            val claveFecha = when (periodo) {
                "Diario" -> fechaCompra
                "Semanal" -> fechaCompra?.with(DayOfWeek.MONDAY)
                "Mensual" -> fechaCompra?.withDayOfMonth(1)
                else -> null
            }

            if (claveFecha != null && claveFecha in mapaPorFechaYUsuario) {
                val cantidad = compra.lista_productos.sumOf { it.cantidad ?: 0 }
                val usuarioMap = mapaPorFechaYUsuario[claveFecha]!!
                usuarioMap[atendedorId] = usuarioMap.getOrDefault(atendedorId, 0) + cantidad
            }
        }

        // Obtener nombres reales por cada usuarioId
        val repositorio = UsuarioRepository(db)
        val mapaIdANombre: Map<String, String> = atendedorIds.associateWith { id ->
            repositorio.obtenerUsuarioPorId(id)?.nombre ?: "Usuario $id"
        }

        // Reemplazar los IDs por nombres reales
        val mapaFinalPorFechaYNombre = mapaPorFechaYUsuario.mapValues { (_, mapaPorId) ->
            mapaPorId.mapKeys { (id, _) ->
                if (id == "Desconocido") "Desconocido" else mapaIdANombre[id] ?: id
            }.toMutableMap()
        }

        val nombresUsuarios = mapaFinalPorFechaYNombre.values.flatMap { it.keys }.toSet()

        val datosPorUsuario = nombresUsuarios.associateWith { nombre ->
            fechas.map { fecha -> mapaFinalPorFechaYNombre[fecha]?.get(nombre)?.toFloat() ?: 0f }
        }

        _uiState.update {
            it.copy(
                fechasGrafico = fechas,
                datosGraficoPorUsuario = datosPorUsuario
            )
        }
    }

    // KPIs
    suspend fun actualizarIndicadores() {
        if (_uiState.value.operacionSeleccionada == "Ventas") {
            cambiarUnidadesVendidas(
                contarUnidadesVendidas()
            )
            cambiarTotalRecaudadoVentas(
                calcularTotalRecaudadoVentas()
            )
            cambiarCantidadCategoriasVentas(
                contarCategoriasUnicasVentas()
            )
            cambiarCantidadProductosUnicosVendidos(
                contarProductosDiferentesVendidos()
            )

        } else if (_uiState.value.operacionSeleccionada == "Compras") {
            cambiarUnidadesCompradas(
                contarUnidadesCompradas()
            )
            cambiarTotalInvertidoCompras(
                calcularTotalInvertidoCompras()
            )
            cambiarCantidadCategoriasCompras(
                contarCategoriasUnicasCompras()
            )
            cambiarCantidadProductosUnicosComprados(
                contarProductosDiferentesComprados()
            )
        }
    }

    fun contarUnidadesVendidas(): Int {
        val listaVentas = _uiState.value.listaVentas
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
        val listaCompras = _uiState.value.listaCompras
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
        val listaVentas = _uiState.value.listaVentas
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
        val listaCompras = _uiState.value.listaCompras
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

    suspend fun contarCategoriasUnicasVentas(): Int {
        val listaVentas = _uiState.value.listaVentas
        val listaCategorias: MutableList<String?> = mutableListOf()
        if (listaVentas != null) {
            listaVentas.forEach { itemVenta ->
                val listaProductosItem = itemVenta.lista_productos
                listaProductosItem.forEach { itemProducto ->
                    if (itemProducto.producto_id != null) {
                        val producto = productoRepository.obtenerProductoPorId(
                            itemProducto.producto_id
                        )
                        if (producto?.categoria_id !in listaCategorias) {
                            listaCategorias.add(producto?.categoria_id)
                        }
                    }
                }
            }
            return listaCategorias.size
        } else {
            return 0
        }
    }

    suspend fun contarCategoriasUnicasCompras(): Int {
        val listaVentas = _uiState.value.listaCompras
        val listaCategorias: MutableList<String?> = mutableListOf()
        if (listaVentas != null) {
            listaVentas.forEach { itemCompra ->
                val listaProductosItem = itemCompra.lista_productos
                listaProductosItem.forEach { itemProducto ->
                    if (itemProducto.producto_id != null) {
                        val producto = productoRepository.obtenerProductoPorId(
                            itemProducto.producto_id
                        )
                        if (producto?.categoria_id !in listaCategorias) {
                            listaCategorias.add(producto?.categoria_id)
                        }
                    }
                }
            }
            return listaCategorias.size
        } else {
            return 0
        }
    }

    fun contarProductosDiferentesVendidos(): Int {
        val listaVentas = _uiState.value.listaVentas
        val listaProductosUnicos: MutableList<String> = mutableListOf()
        if (listaVentas != null) {
            listaVentas.forEach { itemVenta ->
                val listaProductosItem = itemVenta.lista_productos
                listaProductosItem.forEach { itemProducto ->
                    if (itemProducto.producto_id != null) {
                        if (itemProducto.producto_id !in listaProductosUnicos) {
                            listaProductosUnicos.add(itemProducto.producto_id)
                        }
                    }
                }
            }
            return listaProductosUnicos.size
        } else {
            return 0
        }
    }

    fun contarProductosDiferentesComprados(): Int {
        val listaVentas = _uiState.value.listaVentas
        val listaProductosUnicos: MutableList<String> = mutableListOf()
        if (listaVentas != null) {
            listaVentas.forEach { itemCompra ->
                val listaProductosItem = itemCompra.lista_productos
                listaProductosItem.forEach { itemProducto ->
                    if (itemProducto.producto_id != null) {
                        if (itemProducto.producto_id !in listaProductosUnicos) {
                            listaProductosUnicos.add(itemProducto.producto_id)
                        }
                    }
                }
            }
            return listaProductosUnicos.size
        } else {
            return 0
        }
    }

    fun hayDatos(lista: List<Float>?): Boolean {
        return lista?.any { it != 0f } ?: false
    }

}