package com.example.ferretools.viewmodel.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.ferretools.model.database.Categoria
import com.example.ferretools.repository.CategoriaRepository

// Clase sellada para representar el resultado de una operación (éxito o error)
sealed class Result<out T> {
    data class Success<T>(val data: T): Result<T>() // Éxito con datos
    data class Error(val message: String): Result<Nothing>() // Error con mensaje
}

// Estado de la UI para la lista de categorías
// Incluye loading, lista de categorías y error

data class ListaCategoriasUiState(
    val isLoading: Boolean = false, // Indica si está cargando
    val categorias: List<Categoria> = emptyList(), // Lista de categorías
    val error: String? = null // Mensaje de error (si ocurre)
)

// ViewModel para la pantalla de lista de categorías
class ListaCategoriasViewModel(
    private val repo: CategoriaRepository = CategoriaRepository() // Repositorio de categorías (por defecto usa Firestore)
) : ViewModel() {
    // StateFlow privado para el estado interno
    private val _uiState = MutableStateFlow(ListaCategoriasUiState(isLoading = true))
    // StateFlow público e inmutable para que la UI observe
    val uiState: StateFlow<ListaCategoriasUiState> = _uiState.asStateFlow()

    init {
        cargarCategorias() // Al crear el ViewModel, carga las categorías
    }

    // Función para cargar categorías en tiempo real desde Firestore
    fun cargarCategorias() {
        viewModelScope.launch {
            _uiState.value = ListaCategoriasUiState(isLoading = true) // Muestra loading
            // Observa el flujo de categorías del repositorio
            repo.getCategoriasStream().collect { result ->
                // Actualiza el estado según el resultado
                _uiState.value = when (result) {
                    is Result.Success -> ListaCategoriasUiState(categorias = result.data) // Éxito: muestra categorías
                    is Result.Error -> ListaCategoriasUiState(error = result.message) // Error: muestra mensaje
                }
            }
        }
    }

    // Función para agregar una nueva categoría a Firestore
    fun agregarCategoria(nombre: String) {
        viewModelScope.launch {
            repo.agregarCategoria(nombre) // Llama al repositorio para agregar
        }
    }
} 