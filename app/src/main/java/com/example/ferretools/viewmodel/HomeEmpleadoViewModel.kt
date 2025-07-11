package com.example.ferretools.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.repository.NegocioRepository
import com.example.ferretools.utils.SesionUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeEmpleadoViewModel(
    private val negocioRepository: NegocioRepository = NegocioRepository()
) : ViewModel() {
    
    // Estados para el nombre de usuario y negocio
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()
    
    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName.asStateFlow()

    init {
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        val usuario = SesionUsuario.usuario
        if (usuario != null) {
            _userName.value = usuario.nombre
            cargarNombreNegocio(usuario.negocioId)
        }
    }

    private fun cargarNombreNegocio(negocioId: String?) {
        if (negocioId.isNullOrEmpty()) {
            _storeName.value = "Sin negocio asignado"
            return
        }
        
        viewModelScope.launch {
            try {
                val negocio = negocioRepository.obtenerNegocioPorId(negocioId)
                _storeName.value = negocio?.nombre ?: "Negocio no encontrado"
            } catch (e: Exception) {
                _storeName.value = "Error al cargar negocio"
            }
        }
    }
} 