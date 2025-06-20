package com.example.ferretools.utils

import android.net.Uri
import com.example.ferretools.model.enums.RolUsuario

data class UsuarioActual(
    val uid: String,
    val nombre: String,
    val correo: String,
    val celular: String,
    val fotoUri: Uri? = null,
    val rol: RolUsuario,
    val notificacionSolicitudes: Boolean = true
)

object SesionUsuario {
    var usuario: UsuarioActual? = null

    fun iniciarSesion(usuario: UsuarioActual) {
        this.usuario = usuario
    }

    fun cerrarSesion() {
        this.usuario = null
    }
}