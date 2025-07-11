package com.example.ferretools.viewmodel.inventario

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.Result
import com.example.ferretools.model.database.Categoria
import com.example.ferretools.model.database.Producto
import com.example.ferretools.repository.CategoriaRepository
import com.example.ferretools.repository.ProductoRepository
import com.example.ferretools.utils.ReportGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ReporteInventarioUiState(
    val isLoading: Boolean = false, // Indica si está cargando
    val productos: List<Producto> = emptyList(),
    val productosFiltrados: List<Producto> = emptyList(),
    val categorias: List<Categoria> = emptyList(), // Lista de categorías
    val categoriasName: List<String> = emptyList(),
    val error: String? = null, // Mensaje de error (si ocurre)
    val isGeneratingReport: Boolean = false // Indica si se está generando un reporte
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

    fun actualizarGenerandoReporte(generando: Boolean) {
        _uiState.update { it.copy(isGeneratingReport = generando) }
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
    
    // Generar reporte en PDF
    fun generarReportePDF(context: Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                actualizarGenerandoReporte(true)
                
                val productos = _uiState.value.productosFiltrados
                val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                
                // Generar PDF real
                val pdfContent = ReportGenerator.generarPDF(productos, fecha)
                
                // Guardar archivo
                val nombreArchivo = "reporte_inventario_${System.currentTimeMillis()}.pdf"
                val uri = ReportGenerator.guardarArchivo(context, pdfContent, nombreArchivo, "application/pdf")
                
                if (uri != null) {
                    actualizarGenerandoReporte(false)
                    onSuccess("PDF guardado exitosamente")
                } else {
                    actualizarGenerandoReporte(false)
                    onError("Error al guardar el archivo PDF")
                }
                
            } catch (e: Exception) {
                actualizarGenerandoReporte(false)
                onError("Error al generar PDF: ${e.message}")
                Log.e("ReporteInventarioViewModel", "Error generando PDF", e)
            }
        }
    }
    
    // Generar reporte en Excel
    fun generarReporteExcel(context: Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                actualizarGenerandoReporte(true)
                
                val productos = _uiState.value.productosFiltrados
                val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                
                // Generar Excel real
                val excelContent = ReportGenerator.generarExcel(productos, fecha)
                
                // Guardar archivo
                val nombreArchivo = "reporte_inventario_${System.currentTimeMillis()}.xlsx"
                val uri = ReportGenerator.guardarArchivo(context, excelContent, nombreArchivo, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                
                if (uri != null) {
                    actualizarGenerandoReporte(false)
                    onSuccess("Excel guardado exitosamente")
                } else {
                    actualizarGenerandoReporte(false)
                    onError("Error al guardar el archivo Excel")
                }
                
            } catch (e: Exception) {
                actualizarGenerandoReporte(false)
                onError("Error al generar Excel: ${e.message}")
                Log.e("ReporteInventarioViewModel", "Error generando Excel", e)
            }
        }
    }
    
    // Compartir reporte
    fun compartirReporte(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                actualizarGenerandoReporte(true)
                
                val productos = _uiState.value.productosFiltrados
                val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                
                // Generar contenido para compartir
                val contenido = ReportGenerator.generarContenidoCompartir(productos, fecha)
                
                actualizarGenerandoReporte(false)
                onSuccess(contenido)
                
            } catch (e: Exception) {
                actualizarGenerandoReporte(false)
                onError("Error al generar contenido: ${e.message}")
                Log.e("ReporteInventarioViewModel", "Error generando contenido", e)
            }
        }
    }
    
    // Obtener estadísticas del inventario
    fun obtenerEstadisticas(): Map<String, Any> {
        val productos = _uiState.value.productosFiltrados
        return mapOf(
            "totalProductos" to productos.size,
            "valorTotal" to productos.sumOf { it.precio * it.cantidad_disponible },
            "productosBajoStock" to productos.count { it.cantidad_disponible < 10 },
            "categoriasUnicas" to productos.map { it.categoria_id }.distinct().size
        )
    }
} 