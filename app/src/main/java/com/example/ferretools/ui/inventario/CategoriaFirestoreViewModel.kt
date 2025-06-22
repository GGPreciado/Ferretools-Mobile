package com.example.ferretools.ui.inventario

import androidx.lifecycle.ViewModel
import com.example.ferretools.model.database.Categoria
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoriaFirestoreViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    private var listenerRegistration: ListenerRegistration? = null

    init {
        escucharCategorias()
    }

    private fun escucharCategorias() {
        listenerRegistration = db.collection("categorias")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Categoria::class.java)?.copy(id = doc.id)
                    }
                    _categorias.value = lista
                }
            }
    }

    fun agregarCategoriaSiNoExiste(nombre: String, onResult: (String?) -> Unit) {
        val nombreLimpio = nombre.trim()
        if (nombreLimpio.isEmpty()) {
            onResult(null)
            return
        }

        // Verificar si la categoría ya existe
        val categoriaExiste = _categorias.value.find { it.nombre.equals(nombreLimpio, ignoreCase = true) }
        
        if (categoriaExiste != null) {
            onResult(categoriaExiste.id) // Ya existe, devolver su ID
            return
        }

        // Agregar nueva categoría
        val nuevaCategoria = Categoria(nombre = nombreLimpio)
        db.collection("categorias")
            .add(nuevaCategoria)
            .addOnSuccessListener { documentReference ->
                println("DEBUG: Nueva categoría creada con ID: ${documentReference.id}")
                onResult(documentReference.id)
            }
            .addOnFailureListener { 
                println("ERROR: Error al crear categoría")
                onResult(null) 
            }
    }

    fun editarCategoria(id: String, nuevoNombre: String, onResult: (Boolean) -> Unit) {
        val nombreLimpio = nuevoNombre.trim()
        if (nombreLimpio.isEmpty()) {
            onResult(false)
            return
        }

        // Verificar si ya existe otra categoría con ese nombre
        val categoriaExiste = _categorias.value.any { 
            it.nombre.equals(nombreLimpio, ignoreCase = true) && it.id != id 
        }
        
        if (categoriaExiste) {
            onResult(false) // Ya existe otra categoría con ese nombre
            return
        }

        // Actualizar categoría
        db.collection("categorias")
            .document(id)
            .update("nombre", nombreLimpio)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun eliminarCategoria(id: String, onResult: (Boolean) -> Unit) {
        db.collection("categorias")
            .document(id)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
} 