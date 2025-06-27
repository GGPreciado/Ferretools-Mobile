package com.example.ferretools.model.database

import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    val producto_id: String = "",      // ID del documento Firestore (para UI y navegación)
    val nombre: String = "",
    val descripcion: String? = null,
    val precio: Double = 0.0,
    val cantidad_disponible: Int = 0,
    val codigo_barras: String = "",
    val imagen_url: String? = null,
    val categoria_id: String? = null,
    val negocio_id: String = ""        // ID del negocio (para persistencia y filtrado)
)
