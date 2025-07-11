package com.example.ferretools.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.repository.ProductoRepository
import com.example.ferretools.repository.NegocioRepository
import com.example.ferretools.repository.VentaRepository
import com.example.ferretools.model.Result
import com.example.ferretools.ui.components.StockAlert
import com.example.ferretools.utils.SesionUsuario
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeAdminViewModel(
    private val productoRepository: ProductoRepository = ProductoRepository(),
    private val negocioRepository: NegocioRepository = NegocioRepository(),
    private val ventaRepository: VentaRepository = VentaRepository(),
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
    
    // Estados para las estadísticas de la semana
    private val _ventasSemana = MutableStateFlow(0)
    val ventasSemana: StateFlow<Int> = _ventasSemana.asStateFlow()
    
    private val _ingresosSemana = MutableStateFlow(0.0)
    val ingresosSemana: StateFlow<Double> = _ingresosSemana.asStateFlow()

    init {
        cargarDatosUsuario()
        cargarAlertasStock()
        cargarEstadisticasSemana()
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
    
    private fun cargarEstadisticasSemana() {
        viewModelScope.launch {
            val negocioId = SesionUsuario.usuario?.negocioId
            if (negocioId != null) {
                try {
                    // Obtener ventas del negocio
                    val ventasResult = ventaRepository.obtenerVentasPorNegocio(negocioId)
                    if (ventasResult is Result.Success) {
                        val ventas = ventasResult.data
                        
                        // Calcular el inicio de la semana actual (lunes)
                        val hoy = LocalDate.now()
                        val inicioSemana = hoy.with(DayOfWeek.MONDAY)
                        
                        // Filtrar ventas de esta semana (desde lunes hasta hoy)
                        val ventasEstaSemana = ventas.filter { venta ->
                            val fechaVenta = venta.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                            fechaVenta != null && fechaVenta >= inicioSemana && fechaVenta <= hoy
                        }
                        
                        // Calcular estadísticas
                        val cantidadVentas = ventasEstaSemana.size
                        val totalIngresos = ventasEstaSemana.sumOf { it.total ?: 0.0 }
                        
                        _ventasSemana.value = cantidadVentas
                        _ingresosSemana.value = totalIngresos
                        
                        // Debug: imprimir información para verificar
                        println("DEBUG: Ventas esta semana: $cantidadVentas, Ingresos: $totalIngresos")
                        println("DEBUG: Inicio semana: $inicioSemana, Hoy: $hoy")
                        println("DEBUG: Total ventas en BD: ${ventas.size}")
                    } else {
                        // Si no se pudieron obtener las ventas
                        _ventasSemana.value = 0
                        _ingresosSemana.value = 0.0
                        println("DEBUG: Error al obtener ventas: ${ventasResult}")
                    }
                } catch (e: Exception) {
                    // En caso de error, mantener valores en 0
                    _ventasSemana.value = 0
                    _ingresosSemana.value = 0.0
                    println("DEBUG: Excepción al cargar estadísticas: ${e.message}")
                }
            } else {
                // No hay negocio asignado
                _ventasSemana.value = 0
                _ingresosSemana.value = 0.0
                println("DEBUG: No hay negocio asignado")
            }
        }
    }
    
    fun actualizarEstadisticas() {
        cargarEstadisticasSemana()
    }
    
    // Función para actualizar estadísticas después de una nueva venta
    fun actualizarDespuesDeVenta() {
        cargarEstadisticasSemana()
    }
} 