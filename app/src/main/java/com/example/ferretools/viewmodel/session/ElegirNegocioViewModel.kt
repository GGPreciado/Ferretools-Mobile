package com.example.ferretools.viewmodel.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Negocio
import com.example.ferretools.repository.NegocioRepository
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class NegociosUiState {
    object Loading : NegociosUiState()
    data class Success(val negocios: List<Negocio>) : NegociosUiState()
    data class Error(val message: String) : NegociosUiState()
}

class ElegirNegocioViewModel(
    private val negocioRepo: NegocioRepository = NegocioRepository()
) : ViewModel() {
    private val _negociosState = MutableStateFlow<NegociosUiState>(NegociosUiState.Loading)
    val negociosState = _negociosState.asStateFlow()

    private val auth = Firebase.auth

    init {
        cargarNegocios()
    }

    fun cargarNegocios() {
        viewModelScope.launch {
            _negociosState.value = NegociosUiState.Loading
            negocioRepo.getNegociosStream().collect { result ->
                when (result) {
                    is com.example.ferretools.model.Result.Success -> {
                        _negociosState.value = NegociosUiState.Success(result.data)
                    }
                    is com.example.ferretools.model.Result.Error -> {
                        _negociosState.value = NegociosUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun afiliarUsuarioANegocio(negocioId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onError("No hay usuario autenticado")
            return
        }

        viewModelScope.launch {
            val result = negocioRepo.actualizarNegocioUsuario(user.uid, negocioId)
            when (result) {
                is com.example.ferretools.model.Result.Success -> {
                    // Actualizar la sesión local
                    SesionUsuario.actualizarDatos(negocioId = negocioId)
                    onSuccess()
                }
                is com.example.ferretools.model.Result.Error -> {
                    onError(result.message)
                }
            }
        }
    }
} 