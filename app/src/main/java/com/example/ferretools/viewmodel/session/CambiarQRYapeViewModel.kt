package com.example.ferretools.viewmodel.session

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.database.Negocio
import com.example.ferretools.model.registro.CambiarQRYapeUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CambiarQRYapeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CambiarQRYapeUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun updateQrYapeUri(uri: Uri?) {
        _uiState.update { it.copy(qrYapeUri = uri, errorMessage = null, success = false) }
    }

    fun saveQrYape() {
        val userId = auth.currentUser?.uid
        val qrUri = _uiState.value.qrYapeUri
        if (userId == null || qrUri == null) {
            _uiState.update { it.copy(errorMessage = "Debe seleccionar una imagen de QR.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        // Actualiza el campo qrYapeUri en el documento del negocio asociado al usuario
        db.collection("negocios")
            .whereEqualTo("gerenteId", userId)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "No se encontró el negocio asociado.") }
                } else {
                    val negocioDoc = result.documents.first()
                    negocioDoc.reference.update("qrYapeUri", qrUri)
                        .addOnSuccessListener {
                            _uiState.update { it.copy(isLoading = false, success = true) }
                        }
                        .addOnFailureListener { e ->
                            _uiState.update { it.copy(isLoading = false, errorMessage = "Error al guardar: ${e.message}") }
                        }
                }
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al buscar negocio: ${e.message}") }
            }
    }
} 