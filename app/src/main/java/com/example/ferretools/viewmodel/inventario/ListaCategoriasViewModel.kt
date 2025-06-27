package com.example.ferretools.viewmodel.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.ferretools.model.database.Categoria
import com.example.ferretools.repository.CategoriaRepository
import com.example.ferretools.model.Result

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

    // Función para editar una categoría existente
    fun editarCategoria(id: String, nuevoNombre: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repo.editarCategoria(id, nuevoNombre)
            onResult(result)
        }
    }

    // Función para eliminar una categoría existente
    fun eliminarCategoria(id: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repo.eliminarCategoria(id)
            onResult(result)
        }
    }
} 