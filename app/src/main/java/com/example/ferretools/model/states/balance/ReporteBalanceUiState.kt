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

    )