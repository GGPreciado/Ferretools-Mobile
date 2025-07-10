package com.example.ferretools.viewmodel.pedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Pedido
import com.example.ferretools.model.database.ItemUnitario
import com.example.ferretools.model.database.Producto
import com.example.ferretools.model.enums.MetodosPago
import com.example.ferretools.model.Result
import com.example.ferretools.repository.PedidoRepository
import com.example.ferretools.repository.ProductoRepository
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Locale


// Estado centralizado para el flujo de pedidos
data class PedidoUiState(
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

class PedidoViewModel(
    private val pedidoRepository: PedidoRepository = PedidoRepository(),
    private val productoRepository: ProductoRepository = ProductoRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(PedidoUiState())
    val uiState: StateFlow<PedidoUiState> = _uiState.asStateFlow()

    var ultimoPedidoExitoso: PedidoUiState? = null
        private set

    private val _historialPedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val historialPedidos: StateFlow<List<Pedido>> = _historialPedidos.asStateFlow()
    private var historialListener: ListenerRegistration? = null

    private val _productosPorId = MutableStateFlow<Map<String, Producto?>>(emptyMap())
    val productosPorId: StateFlow<Map<String, Producto?>> = _productosPorId.asStateFlow()

    private val _todosPedidosNegocio = MutableStateFlow<List<Pedido>>(emptyList())
    val todosPedidosNegocio: StateFlow<List<Pedido>> = _todosPedidosNegocio.asStateFlow()
    private var todosPedidosListener: ListenerRegistration? = null

    fun agregarProducto(producto: Producto, cantidad: Int = 1) {
        val productosActuales = _uiState.value.productosSeleccionados.toMutableList()
        val productoExistente = productosActuales.find { it.producto_id == producto.producto_id }
        if (productoExistente != null) {
            val nuevaCantidad = (productoExistente.cantidad ?: 0) + cantidad
            val nuevoSubtotal = nuevaCantidad * producto.precio
            val index = productosActuales.indexOf(productoExistente)
            productosActuales[index] = productoExistente.copy(
                cantidad = nuevaCantidad,
                subtotal = nuevoSubtotal
            )
        } else {
            val nuevoItem = ItemUnitario(
                cantidad = cantidad,
                subtotal = cantidad * producto.precio,
                producto_id = producto.producto_id
            )
            productosActuales.add(nuevoItem)
        }
        actualizarProductosConDetalles(productosActuales)
    }

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

    fun eliminarProducto(productoId: String) {
        val productosActuales = _uiState.value.productosSeleccionados.toMutableList()
        productosActuales.removeAll { it.producto_id == productoId }
        actualizarProductosConDetalles(productosActuales)
    }

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

    fun registrarPedido() {
        val usuario = SesionUsuario.usuario
        val negocioId = usuario?.negocioId
        val clienteId = usuario?.uid
        if (negocioId == null || clienteId == null) {
            _uiState.value = _uiState.value.copy(status = PedidoUiState.Status.Error, mensaje = "No hay sesión de usuario o negocio activo")
            return
        }
        if (_uiState.value.productosSeleccionados.isEmpty()) {
            _uiState.value = _uiState.value.copy(status = PedidoUiState.Status.Error, mensaje = "No hay productos seleccionados")
            return
        }
        _uiState.value = _uiState.value.copy(status = PedidoUiState.Status.Loading)
        viewModelScope.launch {
            val pedido = Pedido(
                fecha = _uiState.value.fecha,
                total = _uiState.value.total,
                metodo_pago = _uiState.value.metodoPago,
                lista_productos = _uiState.value.productosSeleccionados,
                negocioId = negocioId,
                clienteId = clienteId,
                estado = "pendiente"
            )
            when (val result = pedidoRepository.registrarPedido(pedido)) {
                is Result.Success -> {
                    ultimoPedidoExitoso = _uiState.value
                    _uiState.value = _uiState.value.copy(status = PedidoUiState.Status.Success)
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(status = PedidoUiState.Status.Error, mensaje = result.message)
            }
        }
    }

    fun cargarHistorialPedidosCliente() {
        val usuario = SesionUsuario.usuario
        if (usuario?.uid.isNullOrEmpty()) return
        historialListener?.remove()
        historialListener = pedidoRepository.db.collection("pedidos")
            .whereEqualTo("clienteId", usuario.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    _historialPedidos.value = emptyList()
                    return@addSnapshotListener
                }
                val pedidos = snapshot.documents.mapNotNull { it.toObject(Pedido::class.java)?.copy(pedidoId = it.id) }
                _historialPedidos.value = pedidos
            }
    }

    fun cargarTodosPedidosNegocio() {
        val negocioId = SesionUsuario.usuario?.negocioId
        if (negocioId.isNullOrEmpty()) return
        todosPedidosListener?.remove()
        todosPedidosListener = pedidoRepository.db.collection("pedidos")
            .whereEqualTo("negocioId", negocioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    _todosPedidosNegocio.value = emptyList()
                    return@addSnapshotListener
                }
                val pedidos = snapshot.documents.mapNotNull { it.toObject(Pedido::class.java)?.copy(pedidoId = it.id) }
                _todosPedidosNegocio.value = pedidos
            }
    }

    fun getProductosPorIds(ids: List<String>) {
        viewModelScope.launch {
            productoRepository.getProductosStream().collect { result ->
                val lista = when (result) {
                    is Result.Success -> result.data
                    else -> emptyList()
                }
                val map = ids.distinct().associateWith { id -> lista.find { it.producto_id == id } }
                _productosPorId.value = map
            }
        }
    }

    fun cancelarPedido(pedidoId: String) {
        viewModelScope.launch {
            pedidoRepository.actualizarEstadoPedido(pedidoId, "cancelado")
        }
    }

    fun prepararPedido(pedidoId: String) {
        viewModelScope.launch {
            pedidoRepository.actualizarEstadoPedido(pedidoId, "preparado")
        }
    }

    override fun onCleared() {
        super.onCleared()
        historialListener?.remove()
        todosPedidosListener?.remove()
    }

    fun resetState() {
        _uiState.value = PedidoUiState()
    }
} 