package com.example.ferretools.viewmodel.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetallesProductoViewModel(
    private val repo: ProductoRepository = ProductoRepository()
) : ViewModel() {
    private val _eliminado = MutableStateFlow<Boolean?>(null)
    val eliminado: StateFlow<Boolean?> = _eliminado.asStateFlow()

    fun eliminarProducto(productoId: String) {
        viewModelScope.launch {
            val result = repo.eliminarProducto(productoId)
            _eliminado.value = result
        }
    }
} 