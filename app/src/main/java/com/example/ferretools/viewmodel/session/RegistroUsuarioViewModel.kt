package com.example.ferretools.viewmodel.session

import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.model.registro.RegistroUsuarioUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class RegistroUsuarioViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(RegistroUsuarioUiState())
    val uiState = _uiState.asStateFlow()

    val db = Firebase.firestore
    val auth = Firebase.auth

    fun setRol(rolUsuario: RolUsuario) {
        _uiState.update {
            it.copy(rolUsuario = rolUsuario)
        }
    }

    private fun updateState(transform: (RegistroUsuarioUiState) -> RegistroUsuarioUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            updated.copy(isFormValid = isFormValid(updated))
        }
    }

    fun updateName(name: String) {
        updateState { it.copy(name = name) }
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

    fun updatePhone(phone: String) {
        updateState { it.copy(phone = phone) }
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

    fun updateConfirmPassword(confirmPassword: String) {
        updateState {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (_uiState.value.password != confirmPassword)
                    "Las contraseñas no coinciden" else null
            )
        }
    }

    fun updateUri(uri: Uri?) {
        _uiState.update {
            it.copy(imageUri = uri)
        }
    }

    fun toggleShowPassword() {
        _uiState.update { it.copy(showPassword = !_uiState.value.showPassword) }
    }

    fun toggleShowConfirmPassword() {
        _uiState.update { it.copy(showConfirmPassword = !_uiState.value.showConfirmPassword) }
    }

    fun areFieldsFilled(state: RegistroUsuarioUiState): Boolean {
        return listOf(
            state.name,
            state.email,
            state.phone,
            state.password,
            state.confirmPassword
        ).all { it.isNotBlank() }
    }

    private fun isFormValid(state: RegistroUsuarioUiState): Boolean {
        return state.emailError == null &&
                state.passwordError == null &&
                state.confirmPasswordError == null &&
                areFieldsFilled(state)
    }

    fun registerUser() {
        auth.createUserWithEmailAndPassword(_uiState.value.email, _uiState.value.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Si el registro es exitoso
                    Log.e("TAG", "Registro exitoso")

                    val uid = auth.currentUser?.uid

                    // --- NUEVO FLUJO PARA ALMACENERO ---
                    if (_uiState.value.rolUsuario == RolUsuario.ALMACENERO) {
                        val userMap = mapOf(
                            "nombre" to _uiState.value.name,
                            "celular" to _uiState.value.phone,
                            "foto" to _uiState.value.imageUri,
                            // Se registra como CLIENTE temporalmente
                            "rol" to RolUsuario.CLIENTE
                        )
                        uid?.let {
                            db.collection("usuarios")
                                .document(uid)
                                .set(userMap)
                                .addOnSuccessListener {
                                    // Crear la solicitud en la colección 'solicitudes'
                                    val solicitudMap = mapOf(
                                        "usuarioId" to uid,
                                        "nombreUsuario" to _uiState.value.name,
                                        "correo" to _uiState.value.email,
                                        "celular" to _uiState.value.phone,
                                        "fotoUri" to (_uiState.value.imageUri?.toString() ?: ""),
                                        "rolSolicitado" to RolUsuario.ALMACENERO.name,
                                        "estado" to "pendiente"
                                    )
                                    db.collection("solicitudes")
                                        .add(solicitudMap)
                                        .addOnSuccessListener {
                                            Log.d("TAG", "Solicitud de almacenero registrada")
                                            _uiState.update {
                                                it.copy(registerSuccessful = true)
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("TAG", "Error al registrar solicitud: ${e.message}")
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("TAG", "Error al guardar en Firestore")
                                }
                        }

                    } else {
                        // --- CÓDIGO ANTERIOR (comentado) ---
                        /*
                        val userMap = mapOf(
                            "nombre" to _uiState.value.name,
                            "celular" to _uiState.value.phone,
                            "foto" to _uiState.value.imageUri,
                            "rol" to _uiState.value.rolUsuario
                        )
                        uid?.let {
                            db.collection("usuarios")
                                .document(uid)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Log.d("TAG", "Usuario guardado en Firestore")
                                    _uiState.update {
                                        it.copy(registerSuccessful = true)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("TAG", "Error al guardar en Firestore")
                                }
                        }
                        */
                        // --- NUEVO FLUJO PARA CLIENTE Y ADMIN ---
                        val userMap = mapOf(
                            "nombre" to _uiState.value.name,
                            "celular" to _uiState.value.phone,
                            "foto" to _uiState.value.imageUri,
                            "rol" to _uiState.value.rolUsuario
                        )
                        uid?.let {
                            db.collection("usuarios")
                                .document(uid)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Log.d("TAG", "Usuario guardado en Firestore")
                                    _uiState.update {
                                        it.copy(registerSuccessful = true)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("TAG", "Error al guardar en Firestore")
                                }
                        }
                    }
                } else {
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            _uiState.update {
                                it.copy(emailError = "Ese correo ya se encuentra en uso")
                            }
                        }
                    }
                    // Si el registro falla
                    Log.e("TAG", "Registro fallido")
                }
            }
    }

}