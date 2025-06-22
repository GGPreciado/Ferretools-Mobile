package com.example.ferretools.model.states.registro

import android.net.Uri
import com.example.ferretools.model.enums.RolUsuario

data class RegistroUsuarioUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val showPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,
    val imageUri: Uri? = null,
    val rolUsuario: RolUsuario = RolUsuario.CLIENTE,
    val emailError: String? = "",
    val passwordError: String? = "",
    val confirmPasswordError: String? = "",
    val areFieldsFilled: Boolean = false,
    val isFormValid: Boolean = false,
    val registerSuccessful: Boolean = false
)