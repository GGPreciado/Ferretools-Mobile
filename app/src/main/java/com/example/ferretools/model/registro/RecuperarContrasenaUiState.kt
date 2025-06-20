package com.example.ferretools.model.registro

data class RecuperarContrasenaUiState(
    val email: String = "",
    val code: String = "",
    val emailError: String? = null,
    val codeError: String? = null,
    val codeSent: Boolean = false,
    val isCodeValid: Boolean = false,
    val errorMessage: String? = null
) 