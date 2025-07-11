package com.example.ferretools.viewmodel.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Compra
import com.example.ferretools.model.database.Venta
import com.example.ferretools.model.database.Producto
import com.example.ferretools.repository.CompraRepository
import com.example.ferretools.repository.VentaRepository
import com.example.ferretools.repository.ProductoRepository
import com.example.ferretools.utils.SesionUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import kotlinx.coroutines.tasks.await

data class BalanceResumen(
    val total: Double,
    val ingresos: Double,
    val egresos: Double
)

data class Movimiento(
    val productos: String,
    val fecha: String,
    val monto: Double,
    val metodo: String,
    val tipo: String // "venta" o "compra"
)

class BalanceViewModel : ViewModel() {
    private val ventaRepository = VentaRepository()
    private val compraRepository = CompraRepository()
    private val productoRepository = ProductoRepository()
    
    private val _resumen = MutableStateFlow(BalanceResumen(0.0, 0.0, 0.0))
    val resumen: StateFlow<BalanceResumen> = _resumen.asStateFlow()
    
    private val _movimientos = MutableStateFlow<List<Movimiento>>(emptyList())
    val movimientos: StateFlow<List<Movimiento>> = _movimientos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    val fechaSeleccionada: StateFlow<LocalDate> = _fechaSeleccionada.asStateFlow()
    
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    
    init {
        cargarBalance()
    }
    
    fun actualizarFecha(fecha: LocalDate) {
        _fechaSeleccionada.value = fecha
        _error.value = null // Limpiar errores anteriores
        cargarBalance()
    }
    
    fun limpiarError() {
        _error.value = null
    }
    
    private fun cargarBalance() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val negocioId = SesionUsuario.usuario?.negocioId
            if (negocioId != null) {
                try {
                    // Cargar ventas
                    val ventasResult = ventaRepository.obtenerVentasPorNegocio(negocioId)
                    val ventas = if (ventasResult is com.example.ferretools.model.Result.Success) {
                        ventasResult.data
                    } else {
                        emptyList()
                    }
                    
                    // Cargar compras
                    val comprasResult = compraRepository.obtenerComprasPorNegocio(negocioId)
                    val compras = if (comprasResult is com.example.ferretools.model.Result.Success) {
                        comprasResult.data
                    } else {
                        emptyList()
                    }
                    
                    // Cargar productos para obtener nombres
                    val productos = productoRepository.obtenerProductosPorNegocio(negocioId).associateBy { it.producto_id }
                    
                    // Calcular resumen
                    val ingresos = ventas.sumOf { it.total ?: 0.0 }
                    val egresos = compras.sumOf { it.total ?: 0.0 }
                    val total = ingresos - egresos
                    
                    _resumen.value = BalanceResumen(total, ingresos, egresos)
                    
                    // Crear lista de movimientos
                    val movimientosList = mutableListOf<Movimiento>()
                    
                    // Agregar ventas como movimientos positivos
                    ventas.forEach { venta ->
                        val fecha = venta.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                        if (fecha != null) {
                            val productosDesc = venta.lista_productos.joinToString(", ") { item ->
                                val producto = productos[item.producto_id]
                                val nombreProducto = producto?.nombre ?: "Producto desconocido"
                                "$nombreProducto x${item.cantidad}" 
                            }
                            movimientosList.add(
                                Movimiento(
                                    productos = productosDesc,
                                    fecha = fecha.format(dateFormatter),
                                    monto = venta.total ?: 0.0,
                                    metodo = venta.metodo_pago.name,
                                    tipo = "venta"
                                )
                            )
                        }
                    }
                    
                    // Agregar compras como movimientos negativos
                    compras.forEach { compra ->
                        val fecha = compra.fecha?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                        if (fecha != null) {
                            val productosDesc = compra.lista_productos.joinToString(", ") { item ->
                                val producto = productos[item.producto_id]
                                val nombreProducto = producto?.nombre ?: "Producto desconocido"
                                "$nombreProducto x${item.cantidad}" 
                            }
                            movimientosList.add(
                                Movimiento(
                                    productos = productosDesc,
                                    fecha = fecha.format(dateFormatter),
                                    monto = -(compra.total ?: 0.0), // Negativo para egresos
                                    metodo = compra.metodo_pago.name,
                                    tipo = "compra"
                                )
                            )
                        }
                    }
                    
                    // Ordenar por fecha (más reciente primero)
                    movimientosList.sortByDescending { it.fecha }
                    
                    _movimientos.value = movimientosList
                    
                } catch (e: Exception) {
                    // Manejar errores
                    _error.value = "Error al cargar datos: ${e.message}"
                    _resumen.value = BalanceResumen(0.0, 0.0, 0.0)
                    _movimientos.value = emptyList()
                }
            } else {
                // No hay negocio asignado
                _resumen.value = BalanceResumen(0.0, 0.0, 0.0)
                _movimientos.value = emptyList()
            }
            
            _isLoading.value = false
        }
    }
    

    
    fun filtrarMovimientos(tipo: String): List<Movimiento> {
        return when (tipo) {
            "Ingresos" -> _movimientos.value.filter { it.monto >= 0 }
            "Egresos" -> _movimientos.value.filter { it.monto < 0 }
            else -> _movimientos.value
        }
    }
} 