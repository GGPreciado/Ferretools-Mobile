package com.example.ferretools.viewmodel.session

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.registro.RecuperarContrasenaUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class RecuperarContrasenaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecuperarContrasenaUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private var verificationCode: String = ""

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    "Correo inválido" else null
            )
        }
    }

    fun updateCode(code: String) {
        _uiState.update {
            it.copy(
                code = code,
                codeError = if (code.length < 6) "El código debe tener 6 dígitos" else null
            )
        }
    }

    fun sendVerificationCode() {
        val email = _uiState.value.email
        
        // Generar código de 6 dígitos
        verificationCode = String.format("%06d", Random.nextInt(0, 1000000))
        
        // Enviar correo con el código
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Guardar el código en el estado
                    _uiState.update {
                        it.copy(
                            codeSent = true,
                            errorMessage = null
                        )
                    }
                    Log.d("RecuperarContrasena", "Código enviado: $verificationCode")
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            _uiState.update {
                                it.copy(
                                    emailError = "No existe una cuenta con este correo",
                                    errorMessage = "No existe una cuenta con este correo"
                                )
                            }
                        }
                        else -> {
                            _uiState.update {
                                it.copy(
                                    errorMessage = "Error al enviar el código: ${task.exception?.message}"
                                )
                            }
                        }
                    }
                }
            }
    }

    fun verifyCode() {
        val code = _uiState.value.code
        
        if (code == verificationCode) {
            _uiState.update {
                it.copy(
                    isCodeValid = true,
                    errorMessage = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    codeError = "Código incorrecto",
                    errorMessage = "El código ingresado es incorrecto"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = RecuperarContrasenaUiState()
    }
} 