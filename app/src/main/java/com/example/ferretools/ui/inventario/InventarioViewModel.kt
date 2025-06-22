package com.example.ferretools.ui.inventario

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf

//open class InventarioViewModel : ViewModel() {
//    private val _categorias = mutableStateListOf<String>()
//    val categorias: List<String> = _categorias
//
//    init {
//        // Agregar categorías predefinidas
//        _categorias.addAll(listOf(
//            "Herramientas Manuales",
//            "Herramientas Eléctricas",
//            "Pinturas y Accesorios",
//            "Plomería",
//            "Electricidad"
//        ))
//    }
//
//    open fun agregarCategoria(nombre: String) {
//        if (nombre.isNotBlank()) {
//            _categorias.add(nombre)
//        }
//    }
//}