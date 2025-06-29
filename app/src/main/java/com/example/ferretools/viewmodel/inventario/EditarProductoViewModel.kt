package com.example.ferretools.viewmodel.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Producto
import com.example.ferretools.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditarProductoViewModel(
    private val repo: ProductoRepository = ProductoRepository()
) : ViewModel() {
    private val _producto = MutableStateFlow<Producto?>(null)
    val producto: StateFlow<Producto?> = _producto.asStateFlow()

    fun cargarProductoPorId(productoId: String) {
        viewModelScope.launch {
            val result = repo.obtenerProductoPorId(productoId)
            _producto.value = result
        }
    }

    fun editarProducto(productoEditado: Producto, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repo.actualizarProducto(productoEditado)
            onResult(result is com.example.ferretools.model.Result.Success)
        }
    }
}