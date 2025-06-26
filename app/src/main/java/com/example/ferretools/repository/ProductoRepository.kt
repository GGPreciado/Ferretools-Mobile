package com.example.ferretools.repository

import com.example.ferretools.model.database.Producto
import com.example.ferretools.viewmodel.inventario.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// Repositorio para manejar operaciones de productos en Firestore
class ProductoRepository(
    // Instancia de la base de datos Firestore (por defecto, la instancia global)
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // Obtiene un flujo (Flow) en tiempo real de la lista de productos
    fun getProductosStream(): Flow<Result<List<Producto>>> = callbackFlow {
        // Listener para escuchar cambios en la colección "productos"
        val listener = db.collection("productos")
            .addSnapshotListener { snapshot, error ->
                // Si ocurre un error, lo envía al flujo como Result.Error
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }
                // Si hay datos, los convierte a objetos Producto y los envía como Result.Success
                if (snapshot != null) {
                    val productos = snapshot.documents.mapNotNull { it.toObject(Producto::class.java) }
                    trySend(Result.Success(productos))
                }
            }
        // Cuando se cierra el flujo, elimina el listener para evitar fugas de memoria
        awaitClose { listener.remove() }
    }

    // Agrega un nuevo producto a Firestore de forma suspendida
    suspend fun agregarProducto(producto: Producto): Result<Unit> {
        return try {
            // Intenta agregar el producto a la colección "productos"
            db.collection("productos").add(producto).await()
            // Si tiene éxito, retorna Result.Success
            Result.Success(Unit)
        } catch (e: Exception) {
            // Si ocurre un error, retorna Result.Error con el mensaje
            Result.Error(e.message ?: "Error al agregar producto")
        }
    }
} 