package com.example.ferretools.ui.inventario

import androidx.lifecycle.ViewModel
import com.example.ferretools.model.database.Producto
import com.example.ferretools.utils.ProductoDisplay
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InventarioFirestoreViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _productos = MutableStateFlow<List<ProductoDisplay>>(emptyList())
    val productos: StateFlow<List<ProductoDisplay>> = _productos

    private var listenerRegistration: ListenerRegistration? = null

    init {
        escucharProductos()
    }

    private fun escucharProductos() {
        val negocioId = SesionUsuario.usuario?.negocioId
        listenerRegistration = db.collection("productos")
            .whereEqualTo("negocio_id", negocioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Manejo de error (puedes exponer un StateFlow de error si lo deseas)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { doc ->
                        val producto = doc.toObject(Producto::class.java)
                        producto?.let {
                            ProductoDisplay(
                                nombre = it.nombre,
                                precio = it.precio,
                                descripcion = it.descripcion,
                                cantidad_disponible = it.cantidad_disponible,
                                producto_id = doc.id
                            )
                        }
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