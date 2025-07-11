package com.example.ferretools.viewmodel.configuracion

import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.states.configuracion.EditarPerfilUiState
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditarPerfilViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(EditarPerfilUiState())
    val uiState = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    init {
        val usuario = SesionUsuario.usuario!!
        _uiState.value = EditarPerfilUiState(
            name = usuario.nombre,
            email = usuario.correo,
            phone = usuario.celular,
            imageRemoteUrl = usuario.fotoUrl
        )
    }

    private fun updateState(transform: (EditarPerfilUiState) -> EditarPerfilUiState) {
        _uiState.update { current ->
            val update = transform(current)

            update.copy(
                isFormValid = isFormValid(update),
                emailEdited = checkEmailEdited(update.email)
            )
        }
    }

    fun updateName(name: String) {
        updateState {
            it.copy(
                name = name,
                nameError = checkNameError(name)
            )
        }
    }

    fun updateEmail(email: String) {
        updateState {
            it.copy(
                email = email,
                emailError = checkEmailError(email)
            )
        }
    }

    fun updatePhone(phone: String) {
        updateState {
            it.copy(
                phone = phone,
                phoneError = checkPhoneError(phone)
            )
        }
    }

    fun updateFotoUri(fotoUri: Uri?) {
        updateState {
            it.copy(imageLocalUri = fotoUri)
        }
    }

    private fun areFieldsFilled(state: EditarPerfilUiState): Boolean {
        return listOf(
            state.name,
            state.phone,
            state.email,
        ).all { it.isNotBlank() }
    }

    private fun checkNameError(name: String): String? {
        if (name.isBlank()) {
            return "Debe rellenar este campo"
        } else {
            return null
        }
    }

    private fun checkEmailError(email: String): String? {
        if (email.isBlank()) {
            return "Debe rellenar este campo"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Correo inválido"
        } else {
            return null
        }
    }

    private fun checkPhoneError(phone: String): String? {
        if (phone.isBlank()) {
            return "Debe rellenar este campo"
        } else {
            return null
        }
    }

    private fun checkEmailEdited(updatedEmail: String): Boolean {
        val currentEmail = auth.currentUser?.email
        return currentEmail != updatedEmail
    }

    fun isFormValid(state: EditarPerfilUiState): Boolean {
        return state.nameError == null &&
                state.emailError == null &&
                state.phoneError == null &&
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

    private fun updateUser(uid: String, fotoUrl: String?) {
        val updatedUserMap = mapOf(
            "nombre" to _uiState.value.name,
            "celular" to _uiState.value.phone,
            "fotoUrl" to fotoUrl
        )

        db.collection("usuarios")
            .document(uid)
            .update(updatedUserMap)
            .addOnSuccessListener {
                Log.d("TAG", "Usuario guardado en Firestore")

                // Actualizar datos de sesión
                SesionUsuario.actualizarDatos(
                        nombre = _uiState.value.name,
                        celular = _uiState.value.phone,
                        fotoUrl = fotoUrl
                )

                _uiState.update { it.copy(editSuccessful = true) }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error al guardar en Firestore: ${e.message}")
            }
    }


    fun editProfile() {
        // Realizar cambios en auth
        val user = auth.currentUser
        user?.verifyBeforeUpdateEmail(_uiState.value.email)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Cambio de correo exitoso
                    // Realizar cambios en bd
                    val uid = SesionUsuario.usuario!!.uid
                    val fotoLocal = _uiState.value.imageLocalUri
                    val fotoRemote = _uiState.value.imageRemoteUrl

                    if (fotoLocal != null) {
                        uploadImage(
                            uri = fotoLocal,
                            userId = uid,
                            onSuccess = { fotoUrl ->
                                updateUser(uid, fotoUrl)
                            },
                            onError = { e ->
                                Log.e("TAG", "Error al subir imagen: ${e.message}")
                            }
                        )
                    } else {
                        updateUser(uid, fotoRemote)
                    }
                } else {
                    // Error en cambio de correo
                    Log.e("DEBUG", task.exception.toString())
                }
            }

    }

}