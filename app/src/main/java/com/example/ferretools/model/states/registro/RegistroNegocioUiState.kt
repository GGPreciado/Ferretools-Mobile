package com.example.ferretools.model.states.registro

import android.net.Uri

data class RegistroNegocioUiState(
    val businessName: String = "",
    val businessType: String = "",
    val address: String = "",
    val ruc: String = "",
    val ownerId: String = "",
    val logoUri: Uri? = null,
    val isFormValid: Boolean = false,
    val registerSuccessful: Boolean = false,
    val error: String? = null
)