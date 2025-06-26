package com.example.ferretools.ui.inventario

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf

// Data class local para el formulario de producto (no confundir con la entidad de base de datos)
data class Producto(
    val codigoBarras: String,
    val nombre: String,
    val precio: String,
    val cantidad: String,
    val categoria: String,
    val descripcion: String
)

// ViewModel para manejar el estado del formulario de producto
class ProductoViewModel : ViewModel() {
    // Campos del formulario como estados mutables
    val nombreProducto = mutableStateOf("") // Nombre del producto
    val precio = mutableStateOf("") // Precio como texto
    val cantidad = mutableStateOf("") // Cantidad como texto
    val descripcion = mutableStateOf("") // Descripción
    val codigoBarras = mutableStateOf("") // Código de barras

    // Solo mantener la categoría seleccionada
    var categoriaSeleccionada = mutableStateOf("") // Categoría seleccionada

    // Limpia todos los campos del formulario
    fun limpiarFormulario() {
        nombreProducto.value = ""
        precio.value = ""
        cantidad.value = ""
        descripcion.value = ""
        codigoBarras.value = ""
        categoriaSeleccionada.value = ""
    }
} 