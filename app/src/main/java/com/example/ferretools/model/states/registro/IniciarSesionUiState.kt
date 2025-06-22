package com.example.ferretools.model.states.registro

data class IniciarSesionUiState(
    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isFormValid: Boolean = false,
    val isCredentialError: Boolean = false,
    val loginSuccessful: Boolean = false
)
