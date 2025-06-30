package com.example.ferretools.viewmodel.session

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.database.Usuario
import com.example.ferretools.model.states.registro.IniciarSesionUiState
import com.example.ferretools.utils.SesionUsuario
import com.example.ferretools.utils.UsuarioActual
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class IniciarSesionViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(IniciarSesionUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // Función para comprobar si todos los campos están llenos después de cambiar cualquier valor
    private fun updateState(transform: (IniciarSesionUiState) -> IniciarSesionUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            if (updated.isCredentialError) {
                updated.copy(
                    emailError = if (!Patterns.EMAIL_ADDRESS.matcher(updated.email).matches())
                        "Correo inválido" else null,
                    passwordError = if (updated.password.length < 6)
                        "Mínimo 6 caracteres" else null,
                    isCredentialError = false,
                    isFormValid = isFormValid(updated)
                )
            } else {
                updated.copy(
                    isFormValid = isFormValid(updated)
                )
            }
        }
    }

    fun updateEmail(email: String) {
        updateState {
            it.copy(
                email = email,
                emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    "Correo inválido" else null
            )
        }
    }

    fun updatePassword(password: String) {
        updateState {
            it.copy(
                password = password,
                passwordError = if (password.length < 6)
                    "Mínimo 6 caracteres" else null
            )
        }
    }

    fun toggleShowPassword() {
        _uiState.update {
            it.copy(showPassword = !it.showPassword)
        }
    }

    private fun areFieldsFilled(state: IniciarSesionUiState): Boolean {
        return listOf(
            state.email,
            state.password
        ).all { it.isNotBlank() }
    }

    fun isFormValid(state: IniciarSesionUiState): Boolean {
        return state.emailError == null && state.passwordError == null && areFieldsFilled(state)
    }

    fun loginUser() {
        auth.signInWithEmailAndPassword(_uiState.value.email, _uiState.value.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Recuperar datos adicionales desde Firestore
                    auth.currentUser?.uid.let {
                        db.collection("usuarios").document(it!!).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val usuario = document.toObject(Usuario::class.java)
                                    // Guardar en el singleton UserSession
                                    SesionUsuario.iniciarSesion(
                                        UsuarioActual(
                                            uid = auth.uid!!,
                                            nombre = usuario!!.nombre,
                                            correo = auth.currentUser?.email!!,
                                            celular = usuario.celular,
                                            fotoUrl = usuario.fotoUrl,
                                            rol = usuario.rol,
                                            negocioId = usuario.negocioId
                                        )
                                    )
                                } else {
                                    Log.e("DEBUG", "El documento no existe")
                                }
                                // DEBUG
                                Log.e("DEBUG", "Usuario: ${SesionUsuario.usuario.toString()}")
                                Log.e("DEBUG", "Usuario: ${auth.currentUser!!.email}")

                                // Logueo exitoso
                                _uiState.update {
                                    it.copy(loginSuccessful = true)
                                }
                            }
                            .addOnFailureListener {
                                Log.e("DEBUG", "Falló el registro del documento")
                            }
                    }
                } else {
                    // En caso de logueo fallido, se captura la excepción de InvalidCredentials
                    // La recomendación es que todos los campos muestren el mismo error
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {

                            // Se le da el mismo error a los campos de email y password
                            /* Activamos el flag de error de credencial para quitar el error de
                            los dos campos a la vez en la función updateState
                            */
                            _uiState.update {
                                it.copy(
                                    emailError = "Correo o contraseña incorrectos",
                                    passwordError = "Correo o contraseña incorrectos",
                                    isCredentialError = true
                                )
                            }
                        }
                    }
                }
            }
    }
}
