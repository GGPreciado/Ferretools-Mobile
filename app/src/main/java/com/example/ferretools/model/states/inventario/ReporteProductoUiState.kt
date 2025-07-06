package com.example.ferretools.model.states.inventario

import com.example.ferretools.model.database.Compra
import com.example.ferretools.model.database.Venta
import java.time.LocalDate

data class ReporteProductoUiState(
    val operacionSeleccionada: String = "Ventas",
    val compras: List<Compra>? = null,
    val ventas: List<Venta>? = null,
    val periodoTemporal: String = "Diario",
    val valoresGrafico: List<Float> = emptyList(),
    val fechasGrafico: List<LocalDate> = emptyList(),

    // KPIs venta
    val unidadesVendidas: Int = 0,
    val totalRecaudadoVentas: Double = 0.0,
    val gananciaPromedioVenta: Double = 0.0,
    val usuarioMayoresVentas: String = "",

    // KPIs compra
    val unidadesCompradas: Int = 0,
    val totalInvertidoCompras: Double = 0.0,
    val precioPromedioCompra: Double = 0.0,
    val usuarioMayoresCompras: String = ""
)
