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

data class ReporteInventarioUiState(
    val isLoading: Boolean = false, // Indica si está cargando
    val productos: List<Producto> = emptyList(),
    val productosFiltrados: List<Producto> = emptyList(),
    val categorias: List<Categoria> = emptyList(), // Lista de categorías
    val categoriasName: List<String> = emptyList(),
    val error: String? = null // Mensaje de error (si ocurre)
)

class ReporteInventarioViewModel(
    private val productoRepository: ProductoRepository = ProductoRepository(),
    private val categoriaRepository: CategoriaRepository = CategoriaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReporteInventarioUiState())
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
    
    // Obtener estadísticas del inventario
//    fun obtenerEstadisticas(): Map<String, Any> {
//        val productos = _productos.value
//        return mapOf(
//            "totalProductos" to productos.size,
//            "valorTotal" to productos.sumOf { it.precio * it.cantidad_disponible },
//            "productosBajoStock" to productos.count { it.cantidad_disponible < 10 },
//            "categoriasUnicas" to productos.map { it.categoria_id }.distinct().size
//        )
//    }
} 