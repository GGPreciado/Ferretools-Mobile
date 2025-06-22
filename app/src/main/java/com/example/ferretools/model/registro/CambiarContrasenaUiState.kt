package com.example.ferretools.model.registro

data class CambiarContrasenaUiState(
    val currentPassword: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val currentPasswordError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val passwordChanged: Boolean = false,
    val errorMessage: String? = null
) 