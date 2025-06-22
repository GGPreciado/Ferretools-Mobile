package com.example.ferretools.model.states.inventario

import com.example.ferretools.model.database.Venta

data class ReporteProductoUiState(
    val ventas: List<Venta>? = null,
    val periodoTemporal: String = "Diario"
)
