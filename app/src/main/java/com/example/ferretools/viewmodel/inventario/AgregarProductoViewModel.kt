package com.example.ferretools.viewmodel.inventario

import android.util.Log
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
import com.example.ferretools.model.Result
import com.example.ferretools.utils.SesionUsuario

// Estado de la UI para el formulario de agregar producto
// Incluye campos del formulario, loading, error, éxito y lista de categorías

data class AgregarProductoUiState(
    val nombre: String = "", // Nombre del producto
    val precio: String = "", // Precio como texto
    val cantidad: String = "", // Cantidad como texto
    val categoriaId: String = "", // ID de la categoría seleccionada
    val descripcion: String = "", // Descripción del producto
    val codigoBarras: String = "", // Código de barras del producto
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
    fun onCategoriaSeleccionadaChanged(id: String) { _uiState.update { it.copy(categoriaId = id) } }
    fun onDescripcionChanged(desc: String) { _uiState.update { it.copy(descripcion = desc) } }
    fun onCodigoBarrasChanged(codigo: String) { 
        android.util.Log.d("AgregarProductoViewModel", "onCodigoBarrasChanged llamado con: $codigo")
        _uiState.update { 
            val newState = it.copy(codigoBarras = codigo)
            android.util.Log.d("AgregarProductoViewModel", "Estado actualizado - codigoBarras: ${newState.codigoBarras}")
            newState
        } 
    }

    // Función para guardar el producto en Firestore
    fun guardarProducto() {
        val state = _uiState.value
        // Validación básica de campos
        if (state.nombre.isBlank() || state.precio.isBlank() || state.cantidad.isBlank() || state.categoriaId.isBlank()) {
            _uiState.update { it.copy(error = "Completa todos los campos") }
            android.util.Log.d("AgregarProductoViewModel", "Error: campos obligatorios vacíos")
            return
        }
        // Validación de código de barras obligatorio y formato
        val codigoBarras = state.codigoBarras.trim()
        if (codigoBarras.isBlank()) {
            _uiState.update { it.copy(error = "El código de barras es obligatorio") }
            android.util.Log.d("AgregarProductoViewModel", "Error: código de barras vacío")
            return
        }
        if (codigoBarras.length < 8) {
            _uiState.update { it.copy(error = "El código de barras debe tener al menos 8 caracteres") }
            android.util.Log.d("AgregarProductoViewModel", "Error: código de barras muy corto")
            return
        }
        // Validación de unicidad (asíncrona)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val existe = productoRepo.existeCodigoBarras(codigoBarras)
            if (existe) {
                _uiState.update { it.copy(isLoading = false, error = "Ya existe un producto con ese código de barras") }
                android.util.Log.d("AgregarProductoViewModel", "Error: código de barras duplicado")
                return@launch
            }
            // Crea el objeto Producto a partir del estado
            val producto = Producto(
                producto_id = "", // Se asignará automáticamente por Firestore
                nombre = state.nombre,
                precio = state.precio.toDoubleOrNull() ?: 0.0,
                cantidad_disponible = state.cantidad.toIntOrNull() ?: 0,
                categoria_id = state.categoriaId,
                descripcion = state.descripcion,
                codigo_barras = codigoBarras,
                negocioId = SesionUsuario.usuario?.negocioId ?: "",
            )
            android.util.Log.d("AgregarProductoViewModel", "Intentando guardar producto: $producto")
            // Llama al repositorio para guardar el producto
            val result = productoRepo.agregarProducto(producto)
            // Actualiza el estado según el resultado
            _uiState.update {
                if (result is Result.Success) {
                    android.util.Log.d("AgregarProductoViewModel", "Producto guardado exitosamente")
                    it.copy(isLoading = false, exito = true)
                } else {
                    android.util.Log.d("AgregarProductoViewModel", "Error al guardar producto: ${(result as Result.Error).message}")
                    it.copy(isLoading = false, error = (result as Result.Error).message)
                }
            }
        }
    }

    // Función para agregar producto con callback (para compatibilidad con la pantalla actual)
    fun agregarProducto(producto: Producto, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = productoRepo.agregarProducto(producto)
            onResult(result is Result.Success)
        }
    }

    // Función para agregar una categoría si no existe (usado en AgregarProducto)
    fun agregarCategoriaSiNoExiste(nombre: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                // Verificar si la categoría ya existe
                val categoriasExistentes = _uiState.value.categorias
                val categoriaExistente = categoriasExistentes.find { 
                    it.nombre.equals(nombre, ignoreCase = true) 
                }
                
                if (categoriaExistente != null) {
                    // La categoría ya existe, devolver su ID
                    Log.d("DEBUG", "Ya existe la categoría")
                    onResult(categoriaExistente.id)
                } else {
                    // La categoría no existe, crearla
                    val result = categoriaRepo.agregarCategoria(nombre)
                    if (result is Result.Success) {
                        // Buscar la categoría recién creada para obtener su ID
                        val categoriasActualizadas = _uiState.value.categorias
                        val nuevaCategoria = categoriasActualizadas.find { 
                            it.nombre.equals(nombre, ignoreCase = true) 
                        }
                        Log.d("DEBUG", "Se creó una nueva categoría")
                        onResult(nuevaCategoria?.id)
                    } else {
                        Log.d("DEBUG", "Error en crear categoría: $result")
                        onResult(null)
                    }
                }
            } catch (e: Exception) {
                Log.d("DEBUG", "${e.message}")
                onResult(null)
            }
        }
    }

    // Función para recargar productos (para compatibilidad con la pantalla actual)
    fun recargarProductos() {
        // Esta función puede llamar a cargarProductos si es necesario
        // Por ahora, no hace nada ya que el flujo es automático
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

    // Función para limpiar todos los campos del formulario
    fun limpiarFormulario() {
        _uiState.update {
            it.copy(
                nombre = "",
                precio = "",
                cantidad = "",
                categoriaId = "",
                descripcion = "",
                codigoBarras = "",
                error = null,
                exito = false
            )
        }
    }
} 