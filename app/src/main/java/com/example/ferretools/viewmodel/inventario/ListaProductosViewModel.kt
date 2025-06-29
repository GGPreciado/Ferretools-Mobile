package com.example.ferretools.viewmodel.inventario

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.Result
import com.example.ferretools.model.database.Categoria
import com.example.ferretools.model.database.Producto
import com.example.ferretools.repository.CategoriaRepository
import com.example.ferretools.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI para la lista de productos
// Incluye loading, lista de productos y error
// Se observa desde la pantalla para mostrar la UI adecuada

data class ListaProductosUiState(
    val isLoading: Boolean = false, // Indica si está cargando
    val productos: List<Producto> = emptyList(),
    val productosFiltrados: List<Producto> = emptyList(),
    val categorias: List<Categoria> = emptyList(), // Lista de categorías
    val categoriasName: List<String> = emptyList(),
    val error: String? = null // Mensaje de error (si ocurre)
)

// ViewModel para la pantalla de lista de productos
class ListaProductosViewModel(
    private val productoRepository: ProductoRepository = ProductoRepository(), // Repositorio de productos (por defecto usa Firestore)
    private val categoriaRepository: CategoriaRepository = CategoriaRepository()
) : ViewModel() {
    // StateFlow privado para el estado interno
    private val _uiState = MutableStateFlow(ListaProductosUiState())
    // StateFlow público e inmutable para que la UI observe
    val uiState = _uiState.asStateFlow()

    init {
        cargarCategorias()
        cargarProductos()
    }

    fun actualizarProductos(productos: List<Producto>) {
        _uiState.update { it.copy(productos = productos) }
    }

    fun actualizarProductosFiltrados(productosFiltrados: List<Producto>) {
        _uiState.update { it.copy(productosFiltrados = productosFiltrados) }
    }

    fun actualizarCategorias(categorias: List<Categoria>) {
        _uiState.update { it.copy(categorias = categorias) }
        actualizarCategoriasName(
            listOf("Todas las categorías") + _uiState.value.categorias.map { it.nombre }
        )
    }

    fun actualizarCategoriasName(categoriasName: List<String>) {
        _uiState.update { it.copy(categoriasName = categoriasName) }
    }

    fun actualizarLoading(loading: Boolean) {
        _uiState.update { it.copy(isLoading = loading) }
    }

    fun actualizarError(error: String) {
        _uiState.update { it.copy(error = error) }
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            actualizarLoading(true) // Muestra loading
            // Observa el flujo de categorías del repositorio
            categoriaRepository.getCategoriasStream().collect { result ->
                // Actualiza el estado según el resultado
                when (result) {
                    is Result.Success -> actualizarCategorias(result.data) // Éxito: muestra categorías
                    is Result.Error -> actualizarError(result.message) // Error: muestra mensaje
                }
            }
        }
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            productoRepository.getProductosStream().collect { result ->
                when (result) {
                    is Result.Success -> {
                        actualizarProductos(result.data)
                        actualizarProductosFiltrados(result.data)
                    }
                    is Result.Error -> {
                        actualizarProductos(emptyList())
                        actualizarProductosFiltrados(emptyList())
                    }
                }
            }
        }
    }

    // Filtrar productos por categoría
    fun filtrarPorCategoria(categoriaId: String) {
        Log.e("DEBUG", "catId: $categoriaId")
        if (categoriaId.isEmpty()) {
            actualizarProductosFiltrados(_uiState.value.productos)
            Log.e("DEBUG", "Empty: ${_uiState.value.productosFiltrados}")
        } else {
            actualizarProductosFiltrados(_uiState.value.productos.filter { it.categoria_id == categoriaId })
            Log.e("DEBUG", "No empty: ${_uiState.value.productosFiltrados}")
        }
    }

    // Función para eliminar un producto por ID
//    fun eliminarProducto(productoId: String, onResult: (Boolean) -> Unit) {
//        viewModelScope.launch {
//            val result = productoRepository.eliminarProducto(productoId)
//            onResult(result)
//        }
//    }

    // Función para filtrar productos por categoría (en memoria)
//    fun filtrarPorCategoria(categoriaId: String): List<Producto> {
//        return uiState.value.productos.filter { it.categoria_id == categoriaId }
//    }
} 