package com.example.ferretools.model.database

data class Negocio(
    val nombre: String = "",
    val tipo: String = "",
    val direccion: String = "",
    val ruc: String = "",
    val logoUrl: String? = null,
    val gerenteId: String? = null,
    val qrYapeUri: Uri? = null
)
