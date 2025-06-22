package com.example.ferretools.model.states.configuracion

import android.net.Uri

data class EditarNegocioUiState(
    val nombreNegocio: String = "",
    val tipoNegocio: String = "",
    val direccionNegocio: String = "",
    val ruc: String = "",
    val fotoLocalUri: Uri? = null,
    val fotoRemotaUrl: String? = null,
    val errorNombre: String? = null,
    val errorDireccion: String? = null,
    val errorTipo: String? = null,
    val errorRuc: String? = null,
    val formsValido: Boolean = true,
    val edicionExitosa: Boolean = false
)
