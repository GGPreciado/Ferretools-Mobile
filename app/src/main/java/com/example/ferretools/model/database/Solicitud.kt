package com.example.ferretools.model.database

import android.net.Uri
import com.example.ferretools.model.enums.RolUsuario

/**
 * Modelo para solicitudes de usuarios que desean ser parte de un negocio.
 */
data class Solicitud(
    val id: String = "",
    val usuarioId: String = "",
    val nombreUsuario: String = "",
    val correo: String = "",
    val celular: String = "",
    val fotoUri: Uri? = null,
    val rolSolicitado: RolUsuario = RolUsuario.ALMACENERO,
    val estado: String = "pendiente", // "pendiente", "aceptada", "rechazada"
    val negocioId: String = "" // Nuevo campo para el id del negocio
) 