package com.example.ferretools.viewmodel.session

import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.database.Usuario
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.model.states.registro.RegistroUsuarioUiState
import com.example.ferretools.utils.SesionUsuario
import com.example.ferretools.utils.UsuarioActual
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class RegistroUsuarioViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(RegistroUsuarioUiState())
    val uiState = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

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

    private fun areFieldsFilled(state: RegistroUsuarioUiState): Boolean {
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

    private fun uploadImage(uri: Uri, userId: String, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        val imageRef = storage.reference.child("usuarios/$userId/perfil.jpg")

        imageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Error al subir imagen")
                }
                imageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                onSuccess(downloadUrl.toString())
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    private fun saveUser(uid: String, fotoUrl: String?) {

        val rolFinal = if (_uiState.value.rolUsuario == RolUsuario.ALMACENERO)
            RolUsuario.CLIENTE
        else
            _uiState.value.rolUsuario

        val userMap = Usuario(
            nombre = _uiState.value.name,
            celular = _uiState.value.phone,
            fotoUrl = fotoUrl,
            rol = rolFinal
        )

        db.collection("usuarios")
            .document(uid)
            .set(userMap)
            .addOnSuccessListener { document ->
                Log.d("TAG", "Usuario guardado en Firestore")

                // Guardar en el singleton UserSession
                SesionUsuario.iniciarSesion(
                    UsuarioActual(
                        uid = auth.uid!!,
                        nombre = userMap.nombre,
                        correo = auth.currentUser?.email!!,
                        celular = userMap.celular,
                        fotoUrl = userMap.fotoUrl,
                        rol = userMap.rol
                    )
                )

                if (_uiState.value.rolUsuario == RolUsuario.ALMACENERO) {
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

                _uiState.update { it.copy(registerSuccessful = true) }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error al guardar en Firestore: ${e.message}")
            }
    }

    fun registerUser() {
        auth.createUserWithEmailAndPassword(_uiState.value.email, _uiState.value.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val fotoUri = _uiState.value.imageUri

                    if (uid != null) {
                        if (fotoUri != null) {
                            // Caso 1: Hay imagen, subirla
                            uploadImage(
                                uri = fotoUri,
                                userId = uid,
                                onSuccess = { fotoUrl ->
                                    saveUser(uid, fotoUrl)
                                },
                                onError = { e ->
                                    Log.e("TAG", "Error al subir imagen: ${e.message}")
                                }
                            )
                        } else {
                            // Caso 2: No hay imagen, guardar null
                            saveUser(uid, null)
                        }
                    } else {
                        Log.e("TAG", "UID es null")
                    }

                } else {
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            _uiState.update {
                                it.copy(emailError = "Ese correo ya se encuentra en uso")
                            }
                        }
                    }
                    Log.e("TAG", "Registro fallido: ${task.exception?.message}")
                }
            }
    }

}