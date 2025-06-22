package com.example.ferretools.utils

import kotlinx.serialization.Serializable

@Serializable
data class ProductoDisplay(
    val producto_id: String = "",
    val nombre: String = "",
    val descripcion: String? = null,
    val precio: Double = 0.0,
    val cantidad_disponible: Int = 0,
    val codigo_barras: String = "",
    val imagen_url: String? = null,
    val categoria_id: String? = null
)
