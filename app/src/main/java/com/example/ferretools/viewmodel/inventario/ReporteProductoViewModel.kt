package com.example.ferretools.viewmodel.inventario

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.database.Venta
import com.example.ferretools.model.states.inventario.ReporteProductoUiState
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

class ReporteProductoViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ReporteProductoUiState())
    val uiState = _uiState.asStateFlow()

    val db = Firebase.firestore

    fun cargarVentasDeProducto(productoId: String) {
        Log.e("DEBUG", "ProductoId: $productoId")
        val negocioId = SesionUsuario.usuario?.negocioId

        if (negocioId != null) {
            db.collection("ventas")
                .whereEqualTo("negocio_id", negocioId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val ventas = snapshot.documents.mapNotNull { it.toObject(Venta::class.java) }
                    Log.e("DEBUG", "Ventas sin filtrar: $ventas")
                    val ventasDelProducto = ventas.filter { venta ->
                        venta.lista_productos.any { it.producto_id == productoId }
                    }
                    _uiState.value = _uiState.value.copy(ventas = ventasDelProducto)
                    Log.e("DEBUG", "Ventas: ${_uiState.value.ventas}")
                }
                .addOnFailureListener { e ->
                    Log.e("DEBUG", "Error: ${e.message}")
                }
        } else {
            Log.e("DEBUG", "Sesión no iniciada")
        }
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

    fun agruparUnidadesPorPeriodo(productoId: String): Pair<List<Float>, List<LocalDate>> {
        val ventas = _uiState.value.ventas ?: return emptyList<Float>() to emptyList()
        val periodo: String = _uiState.value.periodoTemporal
        val now = LocalDate.now()

        return when (periodo) {
            "Diario" -> {
                val dias = (0..6).map { now.minusDays(it.toLong()) }.reversed()
                val mapa = dias.associateWith { 0 }.toMutableMap()

                ventas.forEach { venta ->
                    val fecha = venta.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    if (fecha in mapa.keys) {
                        val unidades = venta.lista_productos.filter { it.producto_id == productoId }
                            .sumOf { it.cantidad ?: 0 }
                        mapa[fecha!!] = mapa[fecha]!! + unidades
                    }
                }

                mapa.values.map { it.toFloat() } to dias
//                    .map { it.format(DateTimeFormatter.ofPattern("dd MMM")) }
            }

            "Semanal" -> {
                val semanas = (0..6).map { now.minusWeeks(it.toLong()).with(DayOfWeek.MONDAY) }.reversed()
                val mapa = semanas.associateWith { 0 }.toMutableMap()

                ventas.forEach { venta ->
                    val fecha = venta.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    val semana = fecha?.with(DayOfWeek.MONDAY)
                    if (semana in mapa.keys) {
                        val unidades = venta.lista_productos.filter { it.producto_id == productoId }
                            .sumOf { it.cantidad ?: 0 }
                        mapa[semana!!] = mapa[semana]!! + unidades
                    }
                }

                mapa.values.map { it.toFloat() } to semanas
//                    .map {
//                    "Sem. ${it.get(ChronoField.ALIGNED_WEEK_OF_YEAR)}"
//                }
            }

            "Mensual" -> {
                val meses = (0..6).map { now.minusMonths(it.toLong()).withDayOfMonth(1) }.reversed()
                val mapa = meses.associateWith { 0 }.toMutableMap()

                ventas.forEach { venta ->
                    val fecha = venta.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    val mes = fecha?.withDayOfMonth(1)
                    if (mes in mapa.keys) {
                        val unidades = venta.lista_productos.filter { it.producto_id == productoId }
                            .sumOf { it.cantidad ?: 0 }
                        mapa[mes!!] = mapa[mes]!! + unidades
                    }
                }

                mapa.values.map { it.toFloat() } to meses
//                    .map { it.format(DateTimeFormatter.ofPattern("MMM yyyy")) }
            }

            else -> emptyList<Float>() to emptyList()
        }
    }

}