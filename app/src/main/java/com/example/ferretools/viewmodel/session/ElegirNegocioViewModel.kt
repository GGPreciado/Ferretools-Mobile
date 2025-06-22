package com.example.ferretools.viewmodel.session

import androidx.lifecycle.ViewModel
import com.example.ferretools.ui.session.NegocioUi
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class NegociosUiState {
    object Loading : NegociosUiState()
    data class Success(val negocios: List<NegocioUi>) : NegociosUiState()
    data class Error(val message: String) : NegociosUiState()
}

class ElegirNegocioViewModel : ViewModel() {
    private val _negociosState = MutableStateFlow<NegociosUiState>(NegociosUiState.Loading)
    val negociosState = _negociosState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    init {
        fetchNegocios()
    }

    fun fetchNegocios() {
        _negociosState.value = NegociosUiState.Loading
        db.collection("negocios").get()
            .addOnSuccessListener { result ->
                val negocios = result.documents.mapNotNull { doc ->
                    val id = doc.id
                    val nombre = doc.getString("nombre") ?: return@mapNotNull null
                    val logoUrl = doc.getString("logoUrl")
                    NegocioUi(id, nombre, logoUrl)
                }
                _negociosState.value = NegociosUiState.Success(negocios)
            }
            .addOnFailureListener { e ->
                _negociosState.value = NegociosUiState.Error(e.message ?: "Error al cargar negocios")
            }
    }

    fun afiliarUsuarioANegocio(negocioId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onError("No hay usuario autenticado")
            return
        }
        db.collection("usuarios").document(user.uid)
            .update("negocioId", negocioId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error al afiliar usuario") }
    }
} 