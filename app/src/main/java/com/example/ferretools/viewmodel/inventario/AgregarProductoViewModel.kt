package com.example.ferretools.viewmodel.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.ferretools.model.database.Producto
import com.example.ferretools.model.database.Categoria
import com.example.ferretools.repository.ProductoRepository
import com.example.ferretools.repository.CategoriaRepository

// Clase sellada para representar el resultado de una operación (éxito o error)
sealed class Result<out T> {
    data class Success<T>(val data: T): Result<T>() // Éxito con datos
    data class Error(val message: String): Result<Nothing>() // Error con mensaje
}

// Estado de la UI para el formulario de agregar producto
// Incluye campos del formulario, loading, error, éxito y lista de categorías

data class AgregarProductoUiState(
    val nombre: String = "", // Nombre del producto
    val precio: String = "", // Precio como texto
    val cantidad: String = "", // Cantidad como texto
    val categoriaId: String = "", // ID de la categoría seleccionada
    val descripcion: String = "", // Descripción del producto
    val isLoading: Boolean = false, // Indica si está guardando
    val error: String? = null, // Mensaje de error
    val exito: Boolean = false, // Indica si se guardó con éxito
    val categorias: List<Categoria> = emptyList() // Lista de categorías disponibles
)

// ViewModel para la pantalla de agregar producto
class AgregarProductoViewModel(
    private val productoRepo: ProductoRepository = ProductoRepository(), // Repositorio de productos
    private val categoriaRepo: CategoriaRepository = CategoriaRepository() // Repositorio de categorías
) : ViewModel() {
    // StateFlow privado para el estado interno
    private val _uiState = MutableStateFlow(AgregarProductoUiState())
    // StateFlow público e inmutable para que la UI observe
    val uiState: StateFlow<AgregarProductoUiState> = _uiState.asStateFlow()

    init {
        cargarCategorias() // Al crear el ViewModel, carga las categorías
    }

    // Funciones para actualizar los campos del formulario
    fun onNombreChanged(nombre: String) { _uiState.update { it.copy(nombre = nombre) } }
    fun onPrecioChanged(precio: String) { _uiState.update { it.copy(precio = precio) } }
    fun onCantidadChanged(cantidad: String) { _uiState.update { it.copy(cantidad = cantidad) } }
    fun onCategoriaChanged(id: String) { _uiState.update { it.copy(categoriaId = id) } }
    fun onDescripcionChanged(desc: String) { _uiState.update { it.copy(descripcion = desc) } }

    // Función para guardar el producto en Firestore
    fun guardarProducto() {
        val state = _uiState.value
        // Validación básica de campos
        if (state.nombre.isBlank() || state.precio.isBlank() || state.cantidad.isBlank() || state.categoriaId.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) } // Muestra loading
            // Crea el objeto Producto a partir del estado
            val producto = Producto(
                nombre = state.nombre,
                precio = state.precio.toDoubleOrNull() ?: 0.0,
                cantidad_disponible = state.cantidad.toIntOrNull() ?: 0,
                categoria_id = state.categoriaId,
                descripcion = state.descripcion
            )
            // Llama al repositorio para guardar el producto
            val result = productoRepo.agregarProducto(producto)
            // Actualiza el estado según el resultado
            _uiState.update {
                if (result is Result.Success) it.copy(isLoading = false, exito = true)
                else it.copy(isLoading = false, error = (result as Result.Error).message)
            }
        }
    }

    // Función para cargar las categorías disponibles desde Firestore
    private fun cargarCategorias() {
        viewModelScope.launch {
            categoriaRepo.getCategoriasStream().collect { result ->
                if (result is Result.Success) {
                    _uiState.update { it.copy(categorias = result.data) }
                }
            }
        }
    }
} 