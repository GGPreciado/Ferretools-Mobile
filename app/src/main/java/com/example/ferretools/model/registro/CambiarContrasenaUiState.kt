package com.example.ferretools.model.registro

data class CambiarContrasenaUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val passwordChanged: Boolean = false,
    val errorMessage: String? = null
) 