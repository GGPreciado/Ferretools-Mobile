package com.example.ferretools.viewmodel.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.ferretools.model.database.Producto
import com.example.ferretools.model.database.Categoria
import com.example.ferretools.repository.CategoriaRepository

class DetallesProductoViewModel(
    private val repo: ProductoRepository = ProductoRepository(),
    private val categoriaRepo: CategoriaRepository = CategoriaRepository()

) : ViewModel() {
    private val _producto = MutableStateFlow<Producto?>(null)
    val producto: StateFlow<Producto?> = _producto.asStateFlow()
    private val _eliminado = MutableStateFlow<Boolean?>(null)
    val eliminado: StateFlow<Boolean?> = _eliminado.asStateFlow()
    private val _categoriaNombre = MutableStateFlow<String?>(null)
    val categoriaNombre: StateFlow<String?> = _categoriaNombre.asStateFlow()

    fun cargarProductoPorId(productoId: String) {
        viewModelScope.launch {
            val result = repo.obtenerProductoPorId(productoId)
            _producto.value = result
            // Si el producto tiene categoria_id, cargar el nombre de la categoría
            val categoriaId = result?.categoria_id
            if (!categoriaId.isNullOrEmpty()) {
                cargarCategoriaPorId(categoriaId)
            } else {
                _categoriaNombre.value = null
            }
        }
    }

    private suspend fun cargarCategoriaPorId(categoriaId: String) {
        val categoria = categoriaRepo.obtenerCategoriaPorId(categoriaId)
        _categoriaNombre.value = categoria?.nombre
    }

    fun eliminarProducto(productoId: String) {
        viewModelScope.launch {
            val result = repo.eliminarProducto(productoId)
            _eliminado.value = result
        }
    }
} 