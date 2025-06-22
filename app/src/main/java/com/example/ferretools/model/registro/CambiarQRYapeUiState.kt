package com.example.ferretools.model.registro

import android.net.Uri

data class CambiarQRYapeUiState(
    val qrYapeUri: Uri? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
) 