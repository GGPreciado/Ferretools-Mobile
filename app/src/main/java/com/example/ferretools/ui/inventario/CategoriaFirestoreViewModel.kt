package com.example.ferretools.ui.inventario

import androidx.lifecycle.ViewModel
import com.example.ferretools.model.database.Categoria
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel para gestionar categorías directamente con Firestore
class CategoriaFirestoreViewModel : ViewModel() {
    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()
    // StateFlow para la lista de categorías
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    // Listener para cambios en la colección de categorías
    private var listenerRegistration: ListenerRegistration? = null

    init {
        escucharCategorias() // Al crear el ViewModel, empieza a escuchar cambios
    }

    // Escucha cambios en la colección "categorias" de Firestore
    private fun escucharCategorias() {
        listenerRegistration = db.collection("categorias")
            .addSnapshotListener { snapshot, error ->
                // Si ocurre un error, no hace nada (podrías agregar manejo de error)
                if (error != null) {
                    return@addSnapshotListener
                }
                // Si hay datos, los convierte a objetos Categoria y actualiza el StateFlow
                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Categoria::class.java)?.copy(id = doc.id)
                    }
                    _categorias.value = lista
                }
            }
    }

    // Agrega una categoría si no existe, y retorna su ID por callback
    fun agregarCategoriaSiNoExiste(nombre: String, onResult: (String?) -> Unit) {
        val nombreLimpio = nombre.trim()
        if (nombreLimpio.isEmpty()) {
            onResult(null)
            return
        }

        // Verifica si la categoría ya existe en la lista local
        val categoriaExiste = _categorias.value.find { it.nombre.equals(nombreLimpio, ignoreCase = true) }
        
        if (categoriaExiste != null) {
            onResult(categoriaExiste.id) // Ya existe, devuelve su ID
            return
        }

        // Si no existe, la agrega a Firestore
        val nuevaCategoria = Categoria(nombre = nombreLimpio)
        db.collection("categorias")
            .add(nuevaCategoria)
            .addOnSuccessListener { documentReference ->
                onResult(documentReference.id) // Devuelve el ID generado
            }
            .addOnFailureListener {
                onResult(null) // Si falla, devuelve null
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

    // Cancela el listener cuando el ViewModel se destruye
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
} 