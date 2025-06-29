package com.example.ferretools.viewmodel.session

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.Result
import com.example.ferretools.model.database.Negocio
import com.example.ferretools.model.states.registro.RegistroNegocioUiState
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.ferretools.repository.NegocioRepository
import kotlinx.coroutines.launch

class RegistroNegocioViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(RegistroNegocioUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val storage =  Firebase.storage
    private val negocioRepository = NegocioRepository()

    // Función para comprobar la validez del forms después de cambiar cualquier valor
    private fun updateState(transform: (RegistroNegocioUiState) -> RegistroNegocioUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            updated.copy(isFormValid = isFormValid(updated))
        }
    }

    fun updateBusinessName(businessName: String) {
        updateState {
            it.copy(businessName = businessName)
        }
    }

    fun updateBusinessType(businessType: String) {
        updateState {
            it.copy(businessType = businessType)
        }
    }

    fun updateAddress(address: String) {
        updateState {
            it.copy(address = address)
        }
    }

    fun updateRuc(ruc: String) {
        updateState {
            it.copy(ruc = ruc)
        }
    }

    fun updateLogoUri(uri: Uri?) {
        updateState {
            it.copy(logoUri = uri)
        }
    }

    private fun uploadImage(
        uri: Uri,
        ownerId: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val imageRef = storage.reference.child("negocios_logos/$ownerId/logo.jpg")

        imageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: Exception("Error al subir logo")
                imageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                onSuccess(downloadUrl.toString())
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    private fun updateUserBusiness(uid: String, negocio_id: String) {
        db.collection("usuarios")
            .document(uid)
            .update("negocio_id", negocio_id)
            .addOnSuccessListener {
                Log.e("FIREBASE", "Usuario actualizado correctamente")
                SesionUsuario.actualizarDatos(negocioId = negocio_id)
                // Marcar como exitoso
                _uiState.update { it.copy(registerSuccessful = true) }
            }
            .addOnFailureListener { exception ->
                Log.e("FIREBASE", "Error: ${exception.message}")
                _uiState.update { it.copy(error = exception.message) }
            }
    }

    private fun saveBusiness(uid: String, logoUrl: String?) {
        val newBusiness = Negocio(
            nombre = _uiState.value.businessName,
            tipo = _uiState.value.businessType,
            direccion = _uiState.value.address,
            ruc = _uiState.value.ruc,
            gerenteId = uid,
            logoUrl = logoUrl
        )

        /*
        val docRef = db.collection("negocios").document()

        docRef.set(newBusiness)
            .addOnSuccessListener {
                Log.e("FIREBASE", "Negocio registrado correctamente")
                updateUserBusiness(uid, docRef.id)
            }
            .addOnFailureListener { exception ->
                Log.e("FIREBASE", "Error: ${exception.message}")
                _uiState.update { it.copy(error = exception.message) }
            }
         */

        // Usar el repositorio para crear el negocio y asegurar el campo id
        viewModelScope.launch {
            val result = negocioRepository.crearNegocio(newBusiness)
            when (result) {
                is Result.Success -> {
                    updateUserBusiness(uid, result.data)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
            }
        }
    }

    fun registerBusiness() {
        val uid = auth.currentUser?.uid
        val logoUri = _uiState.value.logoUri

        if (uid == null) {
            Log.e("FIREBASE", "Usuario no autenticado")
            _uiState.update { it.copy(error = "Usuario no autenticado") }
            return
        }

        // Limpiar error anterior
        _uiState.update { it.copy(error = null) }

        if (logoUri != null) {
            // Si el logo fue seleccionado, subirlo primero
            uploadImage(
                uri = logoUri,
                ownerId = uid,
                onSuccess = { logoUrl ->
                    saveBusiness(uid, logoUrl)
                },
                onError = { e ->
                    Log.e("FIREBASE", "Error al subir logo: ${e.message}")
                    _uiState.update { it.copy(error = e.message) }
                }
            )
        } else {
            // Si no se seleccionó logo, se guarda null
            saveBusiness(uid, null)
        }
    }

    fun isFormValid(state: RegistroNegocioUiState): Boolean {
        return state.businessName != "" &&
                state.businessType != "" &&
                state.address != "" &&
                state.ruc != ""
    }
}