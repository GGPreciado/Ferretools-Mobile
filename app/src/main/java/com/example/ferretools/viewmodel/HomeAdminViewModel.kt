package com.example.ferretools.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.repository.ProductoRepository
import com.example.ferretools.repository.NegocioRepository
import com.example.ferretools.model.Result
import com.example.ferretools.ui.components.StockAlert
import com.example.ferretools.utils.SesionUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeAdminViewModel(
    private val productoRepository: ProductoRepository = ProductoRepository(),
    private val negocioRepository: NegocioRepository = NegocioRepository(),
    private val criticalThreshold: Int = 2,
    private val lowThreshold: Int = 5
) : ViewModel() {
    private val _stockAlerts = MutableStateFlow<List<StockAlert>>(emptyList())
    val stockAlerts: StateFlow<List<StockAlert>> = _stockAlerts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estados para el nombre de usuario y negocio
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()
    
    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName.asStateFlow()

    init {
        cargarDatosUsuario()
        cargarAlertasStock()
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

    private fun cargarAlertasStock() {
        viewModelScope.launch {
            _isLoading.value = true
            productoRepository.getProductosStream().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val productos = result.data
                        val alerts = productos.filter { it.cantidad_disponible < lowThreshold }
                            .map {
                                StockAlert(
                                    product = it.nombre,
                                    units = it.cantidad_disponible,
                                    isLow = it.cantidad_disponible <= criticalThreshold
                                )
                            }
                        _stockAlerts.value = alerts
                    }
                    is Result.Error -> {
                        _stockAlerts.value = emptyList()
                    }
                }
                _isLoading.value = false
            }
        }
    }
} 