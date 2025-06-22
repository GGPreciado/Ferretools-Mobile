package com.example.ferretools.viewmodel.session

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.registro.CambiarContrasenaUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CambiarContrasenaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CambiarContrasenaUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = Firebase.auth

    fun updateCurrentPassword(currentPassword: String) {
        _uiState.update {
            it.copy(
                currentPassword = currentPassword,
                currentPasswordError = if (currentPassword.length < 6)
                    "La contraseña debe tener al menos 6 caracteres" else null
            )
        }
    }

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
        val currentPassword = _uiState.value.currentPassword
        val newPassword = _uiState.value.password

        if (user != null) {
            // Primero verificar que la contraseña actual sea correcta
            val credential = com.google.firebase.auth.EmailAuthProvider
                .getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Si la re-autenticación es exitosa, cambiar la contraseña
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    _uiState.update {
                                        it.copy(
                                            passwordChanged = true,
                                            errorMessage = null
                                        )
                                    }
                                    Log.d("CambiarContrasena", "Contraseña actualizada exitosamente")
                                } else {
                                    when (updateTask.exception) {
                                        is FirebaseAuthRecentLoginRequiredException -> {
                                            _uiState.update {
                                                it.copy(
                                                    errorMessage = "Por seguridad, debes iniciar sesión nuevamente antes de cambiar tu contraseña"
                                                )
                                            }
                                        }
                                        else -> {
                                            _uiState.update {
                                                it.copy(
                                                    errorMessage = "Error al cambiar la contraseña: ${updateTask.exception?.message}"
                                                )
                                            }
                                        }
                                    }
                                    Log.e("CambiarContrasena", "Error al cambiar contraseña", updateTask.exception)
                                }
                            }
                    } else {
                        when (reauthTask.exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                _uiState.update {
                                    it.copy(
                                        currentPasswordError = "La contraseña actual es incorrecta",
                                        errorMessage = "La contraseña actual es incorrecta"
                                    )
                                }
                            }
                            else -> {
                                _uiState.update {
                                    it.copy(
                                        errorMessage = "Error al verificar la contraseña actual: ${reauthTask.exception?.message}"
                                    )
                                }
                            }
                        }
                        Log.e("CambiarContrasena", "Error al re-autenticar", reauthTask.exception)
                    }
                }
        } else {
            _uiState.update {
                it.copy(
                    errorMessage = "No hay usuario autenticado. Por favor, inicia sesión nuevamente"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = CambiarContrasenaUiState()
    }
} 