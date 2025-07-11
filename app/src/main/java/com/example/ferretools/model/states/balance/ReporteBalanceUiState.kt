package com.example.ferretools.model.states.balance

import com.example.ferretools.model.database.Compra
import com.example.ferretools.model.database.Venta
import java.time.LocalDate

data class ReporteBalanceUiState(
    val operacionSeleccionada: String = "Ventas",
    val listaVentas: List<Venta>? = null,
    val listaCompras: List<Compra>? = null,

    // Gráfico
    val tipoGrafico: String = "Barras",
    val periodoTemporal: String = "Diario",

    // Barras
    val datosGrafico: List<Float> = emptyList(),
    val fechasGrafico: List<LocalDate> = emptyList(),

    // Barras Apiladas
    val datosGraficoPorUsuario: Map<String, List<Float>> = emptyMap(),

    // KPIs venta
    val unidadesVendidas: Int = 0,
    val totalRecaudadoVentas: Double = 0.0,
    val cantidadCategoriasUnicasVentas: Int = 0,
    val cantidadProductosUnicosVendidos: Int = 0,

    // KPIs compra
    val unidadesCompradas: Int = 0,
    val totalInvertidoCompras: Double = 0.0,
    val cantidadCategoriasUnicasCompras: Int = 0,
    val cantidadProductosUnicosComprados: Int = 0,
)