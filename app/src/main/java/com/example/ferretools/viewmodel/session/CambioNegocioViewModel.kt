package com.example.ferretools.viewmodel.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.repository.NegocioRepository
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar el cambio de negocio del usuario
 * Notifica a otros ViewModels cuando el negocio cambia
 */
class CambioNegocioViewModel(
    private val negocioRepo: NegocioRepository = NegocioRepository()
) : ViewModel() {
    
    private val _negocioActual = MutableStateFlow<String?>(null)
    val negocioActual: StateFlow<String?> = _negocioActual.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val auth = Firebase.auth
    
    init {
        // Inicializar con el negocio actual de la sesión
        _negocioActual.value = SesionUsuario.usuario?.negocioId
    }
    
    /**
     * Cambia el negocio del usuario
     * @param nuevoNegocioId ID del nuevo negocio
     * @param onSuccess Callback cuando el cambio es exitoso
     * @param onError Callback cuando hay un error
     */
    fun cambiarNegocio(
        nuevoNegocioId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onError("No hay usuario autenticado")
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = negocioRepo.actualizarNegocioUsuario(user.uid, nuevoNegocioId)
                when (result) {
                    is com.example.ferretools.model.Result.Success -> {
                        // Actualizar la sesión local
                        SesionUsuario.actualizarDatos(negocioId = nuevoNegocioId)
                        _negocioActual.value = nuevoNegocioId
                        onSuccess()
                    }
                    is com.example.ferretools.model.Result.Error -> {
                        _error.value = result.message
                        onError(result.message)
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Error desconocido al cambiar negocio"
                _error.value = errorMessage
                onError(errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Verifica si el usuario tiene un negocio asignado
     */
    fun tieneNegocioAsignado(): Boolean {
        return !_negocioActual.value.isNullOrEmpty()
    }
    
    /**
     * Obtiene el ID del negocio actual
     */
    fun obtenerNegocioActual(): String? {
        return _negocioActual.value
    }
    
    /**
     * Limpia el negocio actual (para logout)
     */
    fun limpiarNegocio() {
        _negocioActual.value = null
        _error.value = null
    }
} 