package com.example.ferretools.ui.inventario

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf

data class Producto(
    val codigoBarras: String,
    val nombre: String,
    val precio: String,
    val cantidad: String,
    val categoria: String,
    val descripcion: String
)

class ProductoViewModel : ViewModel() {
    val nombreProducto = mutableStateOf("")
    val precio = mutableStateOf("")
    val cantidad = mutableStateOf("")
    val descripcion = mutableStateOf("")
    val codigoBarras = mutableStateOf("")

    // Solo mantener la categoría seleccionada
    var categoriaSeleccionada = mutableStateOf("")

    fun limpiarFormulario() {
        nombreProducto.value = ""
        precio.value = ""
        cantidad.value = ""
        descripcion.value = ""
        codigoBarras.value = ""
        categoriaSeleccionada.value = ""
    }
} 