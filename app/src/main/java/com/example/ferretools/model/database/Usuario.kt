package com.example.ferretools.model.database

import com.example.ferretools.model.enums.RolUsuario
import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val nombre: String = "",
    val celular: String = "",
    val fotoUrl: String? = null,
    val rol: RolUsuario = RolUsuario.CLIENTE,
    val negocioId: String? = null
)
