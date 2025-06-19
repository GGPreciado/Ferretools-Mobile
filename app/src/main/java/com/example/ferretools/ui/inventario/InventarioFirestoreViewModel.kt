package com.example.ferretools.ui.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Producto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventarioFirestoreViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private var listenerRegistration: ListenerRegistration? = null

    init {
        escucharProductos()
    }

    private fun escucharProductos() {
        listenerRegistration = db.collection("productos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Manejo de error (puedes exponer un StateFlow de error si lo deseas)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Producto::class.java)
                    }
                    _productos.value = lista
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun agregarProducto(producto: Producto, onResult: (Boolean) -> Unit) {
        db.collection("productos")
            .add(producto)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
} 