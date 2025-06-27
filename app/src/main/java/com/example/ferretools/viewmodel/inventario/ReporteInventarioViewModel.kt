package com.example.ferretools.viewmodel.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Producto
import com.example.ferretools.repository.ProductoRepository
import com.example.ferretools.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReporteInventarioViewModel(
    private val productoRepository: ProductoRepository = ProductoRepository()
) : ViewModel() {
    
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()
    
    private val _productosFiltrados = MutableStateFlow<List<Producto>>(emptyList())
    val productosFiltrados: StateFlow<List<Producto>> = _productosFiltrados.asStateFlow()
    
    init {
        cargarProductos()
    }
    
    private fun cargarProductos() {
        viewModelScope.launch {
            productoRepository.getProductosStream().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _productos.value = result.data
                        _productosFiltrados.value = result.data
                    }
                    is Result.Error -> {
                        _productos.value = emptyList()
                        _productosFiltrados.value = emptyList()
                    }
                }
            }
        }
    }
    
    // Filtrar productos por categoría
    fun filtrarPorCategoria(categoriaId: String) {
        if (categoriaId.isEmpty()) {
            _productosFiltrados.value = _productos.value
        } else {
            _productosFiltrados.value = _productos.value.filter { it.categoria_id == categoriaId }
        }
    }
    
    // Obtener estadísticas del inventario
    fun obtenerEstadisticas(): Map<String, Any> {
        val productos = _productos.value
        return mapOf(
            "totalProductos" to productos.size,
            "valorTotal" to productos.sumOf { it.precio * it.cantidad_disponible },
            "productosBajoStock" to productos.count { it.cantidad_disponible < 10 },
            "categoriasUnicas" to productos.map { it.categoria_id }.distinct().size
        )
    }
} 