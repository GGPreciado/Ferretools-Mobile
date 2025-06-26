package com.example.ferretools.viewmodel.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.example.ferretools.model.database.Producto
import com.example.ferretools.repository.ProductoRepository

// Clase sellada para representar el resultado de una operación (éxito o error)
sealed class Result<out T> {
    data class Success<T>(val data: T): Result<T>() // Éxito con datos
    data class Error(val message: String): Result<Nothing>() // Error con mensaje
}

// Estado de la UI para la lista de productos
// Incluye loading, lista de productos y error
// Se observa desde la pantalla para mostrar la UI adecuada

data class ListaProductosUiState(
    val isLoading: Boolean = false, // Indica si está cargando
    val productos: List<Producto> = emptyList(), // Lista de productos
    val error: String? = null // Mensaje de error (si ocurre)
)

// ViewModel para la pantalla de lista de productos
class ListaProductosViewModel(
    private val repo: ProductoRepository = ProductoRepository() // Repositorio de productos (por defecto usa Firestore)
) : ViewModel() {
    // StateFlow privado para el estado interno
    private val _uiState = MutableStateFlow(ListaProductosUiState(isLoading = true))
    // StateFlow público e inmutable para que la UI observe
    val uiState: StateFlow<ListaProductosUiState> = _uiState.asStateFlow()

    init {
        cargarProductos() // Al crear el ViewModel, carga los productos
    }

    // Función para cargar productos en tiempo real desde Firestore
    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.value = ListaProductosUiState(isLoading = true) // Muestra loading
            // Observa el flujo de productos del repositorio
            repo.getProductosStream().collect { result ->
                // Actualiza el estado según el resultado
                _uiState.value = when (result) {
                    is Result.Success -> ListaProductosUiState(productos = result.data) // Éxito: muestra productos
                    is Result.Error -> ListaProductosUiState(error = result.message) // Error: muestra mensaje
                }
            }
        }
    }
} 