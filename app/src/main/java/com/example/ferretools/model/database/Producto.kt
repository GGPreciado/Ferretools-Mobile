package com.example.ferretools.model.database

data class Producto(
    val nombre: String = "",
    val descripcion: String? = null,
    val precio: Double = 0.0,
    val cantidad_disponible: Int = 0,
    val codigo_barras: String = "",
    val imagen_url: String? = null,
    val categoria_id: String? = null,
)
