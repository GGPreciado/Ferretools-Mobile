package com.example.ferretools.model.states.configuracion

import android.net.Uri

data class EditarPerfilUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val imageRemoteUrl: String? = null,
    val imageLocalUri: Uri? = null,
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val isFormValid: Boolean = true,
    val emailEdited: Boolean = false,
    val editSuccessful: Boolean = false
)
