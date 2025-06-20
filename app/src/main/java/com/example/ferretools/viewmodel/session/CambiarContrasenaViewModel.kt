package com.example.ferretools.viewmodel.session

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.registro.CambiarContrasenaUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CambiarContrasenaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CambiarContrasenaUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = Firebase.auth

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = if (password.length < 6)
                    "La contraseña debe tener al menos 6 caracteres" else null
            )
        }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (_uiState.value.password != confirmPassword)
                    "Las contraseñas no coinciden" else null
            )
        }
    }

    fun changePassword() {
        val user = auth.currentUser
        val newPassword = _uiState.value.password

        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.update {
                            it.copy(
                                passwordChanged = true,
                                errorMessage = null
                            )
                        }
                        Log.d("CambiarContrasena", "Contraseña actualizada exitosamente")
                    } else {
                        _uiState.update {
                            it.copy(
                                errorMessage = "Error al cambiar la contraseña: ${task.exception?.message}"
                            )
                        }
                    }
                }
        } else {
            _uiState.update {
                it.copy(
                    errorMessage = "No hay usuario autenticado"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = CambiarContrasenaUiState()
    }
} 