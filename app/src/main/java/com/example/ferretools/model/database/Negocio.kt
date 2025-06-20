package com.example.ferretools.model.database

import android.net.Uri

data class Negocio(
    val nombre: String,
    val tipo: String,
    val direccion: String,
    val ruc: String,
    val logoUri: Uri? = null,
    val gerenteId: String?,
    val qrYapeUri: Uri? = null,
)
