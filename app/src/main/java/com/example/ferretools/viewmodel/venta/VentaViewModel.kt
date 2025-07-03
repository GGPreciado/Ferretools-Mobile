package com.example.ferretools.viewmodel.venta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Venta
import com.example.ferretools.model.database.ItemUnitario
import com.example.ferretools.model.database.Producto
import com.example.ferretools.model.enums.MetodosPago
import com.example.ferretools.model.Result
import com.example.ferretools.repository.VentaRepository
import com.example.ferretools.repository.ProductoRepository
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class VentaUiState(
    val productosSeleccionados: List<ItemUnitario> = emptyList(),
    val productosConDetalles: List<Pair<ItemUnitario, Producto?>> = emptyList(),
    val metodoPago: MetodosPago = MetodosPago.Efectivo,
    val fecha: Timestamp = Timestamp.now(),
    val total: Double = 0.0,
    val status: Status = Status.Idle,
    val mensaje: String? = null
) {
    enum class Status { Idle, Loading, Success, Error }
}

class VentaViewModel(
    private val ventaRepository: VentaRepository = VentaRepository(),
    private val productoRepository: ProductoRepository = ProductoRepository()
) : ViewModel() {
    // Estado centralizado y observable
    private val _uiState = MutableStateFlow(VentaUiState())
    val uiState: StateFlow<VentaUiState> = _uiState

    // Nueva propiedad para guardar la última venta exitosa
    var ultimaVentaExitosa: VentaUiState? = null
        private set

    /**
     * Agrega un producto al carrito de venta
     */
    fun agregarProducto(producto: Producto, cantidad: Int = 1) {
        val productosActuales = _uiState.value.productosSeleccionados.toMutableList()
        
        // Verificar si el producto ya está en el carrito
        val productoExistente = productosActuales.find { it.producto_id == producto.producto_id }
        
        if (productoExistente != null) {
            // Actualizar cantidad del producto existente
            val nuevaCantidad = (productoExistente.cantidad ?: 0) + cantidad
            val nuevoSubtotal = nuevaCantidad * producto.precio
            
            val index = productosActuales.indexOf(productoExistente)
            productosActuales[index] = productoExistente.copy(
                cantidad = nuevaCantidad,
                subtotal = nuevoSubtotal
            )
        } else {
            // Agregar nuevo producto
            val nuevoItem = ItemUnitario(
                cantidad = cantidad,
                subtotal = cantidad * producto.precio,
                producto_id = producto.producto_id
            )
            productosActuales.add(nuevoItem)
        }
        
        actualizarProductosConDetalles(productosActuales)
    }

    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    fun actualizarCantidadProducto(productoId: String, nuevaCantidad: Int) {
        val productosActuales = _uiState.value.productosSeleccionados.toMutableList()
        val index = productosActuales.indexOfFirst { it.producto_id == productoId }
        
        if (index != -1) {
            val producto = productosActuales[index]
            val productoDetalle = _uiState.value.productosConDetalles.find { it.first.producto_id == productoId }?.second
            
            if (nuevaCantidad > 0) {
                val nuevoSubtotal = nuevaCantidad * (productoDetalle?.precio ?: 0.0)
                productosActuales[index] = producto.copy(
                    cantidad = nuevaCantidad,
                    subtotal = nuevoSubtotal
                )
            } else {
                productosActuales.removeAt(index)
            }
            
            actualizarProductosConDetalles(productosActuales)
        }
    }

    /**
     * Elimina un producto del carrito
     */
    fun eliminarProducto(productoId: String) {
        val productosActuales = _uiState.value.productosSeleccionados.toMutableList()
        productosActuales.removeAll { it.producto_id == productoId }
        actualizarProductosConDetalles(productosActuales)
    }

    /**
     * Actualiza los productos con sus detalles y calcula el total
     */
    private fun actualizarProductosConDetalles(nuevosProductos: List<ItemUnitario>) {
        viewModelScope.launch {
            val productos = productoRepository.getProductosStream().collect { result ->
                val lista = when (result) {
                    is Result.Success -> result.data
                    else -> emptyList()
                }
                val detalles = nuevosProductos.map { item ->
                    item to lista.find { it.producto_id == item.producto_id }
                }
                val total = detalles.sumOf { (item, producto) -> (item.cantidad ?: 0) * (producto?.precio ?: 0.0) }
                _uiState.value = _uiState.value.copy(
                    productosSeleccionados = nuevosProductos,
                    productosConDetalles = detalles,
                    total = total
                )
            }
        }
    }

    fun cambiarMetodoPago(metodo: MetodosPago) {
        _uiState.value = _uiState.value.copy(metodoPago = metodo)
    }

    fun cambiarFecha(fecha: Timestamp) {
        _uiState.value = _uiState.value.copy(fecha = fecha)
    }

    /**
     * Registra la venta en la base de datos
     */
    fun registrarVenta() {
        val usuario = SesionUsuario.usuario
        val negocioId = usuario?.negocioId
        
        if (negocioId == null) {
            _uiState.value = _uiState.value.copy(status = VentaUiState.Status.Error, mensaje = "No hay sesión de negocio activo")
            return
        }

        if (_uiState.value.productosSeleccionados.isEmpty()) {
            _uiState.value = _uiState.value.copy(status = VentaUiState.Status.Error, mensaje = "No hay productos seleccionados")
            return
        }

        _uiState.value = _uiState.value.copy(status = VentaUiState.Status.Loading)
        
        viewModelScope.launch {
            val venta = Venta(
                fecha = _uiState.value.fecha,
                total = _uiState.value.total,
                metodo_pago = _uiState.value.metodoPago,
                lista_productos = _uiState.value.productosSeleccionados,
                negocioId = negocioId,
                atendedor_id = usuario?.uid
            )
            
            when (val result = ventaRepository.registrarVenta(venta)) {
                is Result.Success -> {
                    ultimaVentaExitosa = _uiState.value // Guarda la venta antes de limpiar
                    _uiState.value = _uiState.value.copy(status = VentaUiState.Status.Success)
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(status = VentaUiState.Status.Error, mensaje = result.message)
            }
        }
    }

    fun resetState() {
        _uiState.value = VentaUiState()
    }
} 