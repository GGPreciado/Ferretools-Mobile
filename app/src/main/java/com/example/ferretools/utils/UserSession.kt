package com.example.ferretools.utils

import com.example.ferretools.model.enums.RolUsuario

data class UsuarioActual(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val celular: String = "",
    val fotoUrl: String? = null,
    val rol: RolUsuario = RolUsuario.CLIENTE,
    val negocioId: String? = null,
    val notificacionSolicitudes: Boolean = true
)

object SesionUsuario {
    private var _usuario: UsuarioActual? = null
    val usuario: UsuarioActual?
        get() = _usuario

    var rolDeseado: RolUsuario? = null
    var negocioId: String? = null

    fun iniciarSesion(usuario: UsuarioActual) {
        this._usuario = usuario
    }

    fun actualizarDatos(
        nombre: String? = null,
        correo: String? = null,
        celular: String? = null,
        fotoUrl: String? = null,
        rol: RolUsuario? = null,
        negocioId: String? = null,
        notificacionSolicitudes: Boolean? = null,
        rolDeseado: RolUsuario? = null
    ) {
        _usuario = _usuario?.copy(
            nombre = nombre ?: _usuario!!.nombre,
            correo = correo ?: _usuario!!.correo,
            celular = celular ?: _usuario!!.celular,
            fotoUrl = fotoUrl ?: _usuario!!.fotoUrl,
            negocioId = negocioId ?: _usuario!!.negocioId,
            notificacionSolicitudes = notificacionSolicitudes ?: _usuario!!.notificacionSolicitudes
        )
        if (rolDeseado != null) this.rolDeseado = rolDeseado
        if (negocioId != null) this.negocioId = negocioId
    }

    fun cerrarSesion() {
        this._usuario = null
    }
}