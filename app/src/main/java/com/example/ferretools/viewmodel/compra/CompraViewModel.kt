package com.example.ferretools.viewmodel.compra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Compra
import com.example.ferretools.model.database.ItemUnitario
import com.example.ferretools.model.database.Producto
import com.example.ferretools.model.enums.MetodosPago
import com.example.ferretools.model.Result
import com.example.ferretools.repository.CompraRepository
import com.example.ferretools.repository.ProductoRepository
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CompraUiState(
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

class CompraViewModel(
    private val compraRepository: CompraRepository = CompraRepository(),
    private val productoRepository: ProductoRepository = ProductoRepository()
) : ViewModel() {
    // Estado centralizado y observable
    private val _uiState = MutableStateFlow(CompraUiState())
    val uiState: StateFlow<CompraUiState> = _uiState

    // Nueva propiedad para guardar la última compra exitosa
    var ultimaCompraExitosa: CompraUiState? = null
        private set

    // Actualizar productos seleccionados
    fun agregarProducto(item: ItemUnitario) {
        val existente = _uiState.value.productosSeleccionados.find { it.producto_id == item.producto_id }
        val nuevosProductos = if (existente != null) {
            _uiState.value.productosSeleccionados.map {
                if (it.producto_id == item.producto_id)
                    it.copy(cantidad = (it.cantidad ?: 0) + 1, subtotal = ((it.cantidad ?: 0) + 1) * (item.subtotal ?: 0.0))
                else it
            }
        } else {
            _uiState.value.productosSeleccionados + item
        }
        actualizarProductosConDetalles(nuevosProductos)
    }
    fun quitarProducto(productoId: String) {
        val nuevosProductos = _uiState.value.productosSeleccionados.filter { it.producto_id != productoId }
        actualizarProductosConDetalles(nuevosProductos)
    }
    fun cambiarCantidad(productoId: String, nuevaCantidad: Int, precio: Double) {
        val nuevosProductos = _uiState.value.productosSeleccionados.map {
            if (it.producto_id == productoId)
                it.copy(cantidad = nuevaCantidad, subtotal = nuevaCantidad * precio)
            else it
        }
        actualizarProductosConDetalles(nuevosProductos)
    }
    private fun actualizarProductosConDetalles(nuevosProductos: List<ItemUnitario>) {
        // Obtener detalles de productos (puedes optimizar esto si tienes los productos en memoria)
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
    fun registrarCompra() {
        val usuario = SesionUsuario.usuario
        val negocioId = usuario?.negocioId
        val usuarioId = usuario?.uid
        if (negocioId == null || usuarioId == null) {
            _uiState.value = _uiState.value.copy(status = CompraUiState.Status.Error, mensaje = "No hay sesión de usuario o negocio activo")
            return
        }
        _uiState.value = _uiState.value.copy(status = CompraUiState.Status.Loading)
        viewModelScope.launch {
            val compra = Compra(
                fecha = _uiState.value.fecha,
                total = _uiState.value.total,
                metodo_pago = _uiState.value.metodoPago,
                lista_productos = _uiState.value.productosSeleccionados,
                negocioId = FirebaseFirestore.getInstance().collection("negocios").document(negocioId),
                usuarioId = FirebaseFirestore.getInstance().collection("usuarios").document(usuarioId)
            )
            when (val result = compraRepository.registrarCompra(compra)) {
                is Result.Success -> {
                    ultimaCompraExitosa = _uiState.value // Guarda la compra antes de limpiar
                    _uiState.value = _uiState.value.copy(status = CompraUiState.Status.Success)
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(status = CompraUiState.Status.Error, mensaje = result.message)
            }
        }
    }
    fun resetState() {
        _uiState.value = CompraUiState()
    }
} 