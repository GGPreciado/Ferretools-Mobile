package com.example.ferretools.model.database

import android.net.Uri

data class Negocio(
    val id: String = "",
    val nombre: String = "",
    val tipo: String = "",
    val direccion: String = "",
    val ruc: String = "",
    val logoUrl: String? = null,
    val gerenteId: String? = null,
    val qrYapeUri: Uri? = null
)
