package com.example.ferretools.repository

import com.example.ferretools.model.database.Producto
import com.example.ferretools.model.Result
import com.example.ferretools.utils.SesionUsuario
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
    fun getProductosStream(): Flow<Result<List<Producto>>> = callbackFlow {
        val negocioId = SesionUsuario.usuario?.negocioId
        if (negocioId.isNullOrEmpty()) {
            trySend(Result.Error("No hay sesión de usuario o negocio activo"))
            close() // Cierra el flujo inmediatamente
            return@callbackFlow
        }
        val listener = db.collection("productos")
            .whereEqualTo("negocioId", negocioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val productos = snapshot.documents.mapNotNull { it.toObject(Producto::class.java) }
                    trySend(Result.Success(productos))
                }
            }
        awaitClose { listener.remove() }
    }

    // Agrega un nuevo producto a Firestore de forma suspendida
    suspend fun agregarProducto(producto: Producto): Result<Unit> {
        return try {
            // Crear un mapa sin producto_id para guardar en Firestore
//            val productoParaGuardar = producto.copy(producto_id = "")
            // Intenta agregar el producto a la colección "productos"
            val documentReference = db.collection("productos").document()
            val productoParaGuardar = producto.copy(producto_id = documentReference.id)
            documentReference.set(productoParaGuardar).await()
            // Si tiene éxito, retorna Result.Success
            Result.Success(Unit)
        } catch (e: Exception) {
            // Si ocurre un error, retorna Result.Error con el mensaje
            Result.Error(e.message ?: "Error al agregar producto")
        }
    }

    // Elimina un producto por su ID
    suspend fun eliminarProducto(productoId: String): Boolean {
        return try {
            db.collection("productos")
                .document(productoId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Actualiza un producto existente en Firestore
    suspend fun actualizarProducto(producto: Producto): Result<Unit> {
        return try {
            // Crear un mapa sin producto_id para guardar en Firestore
//            val productoParaGuardar = producto.copy(producto_id = "")
            // Intenta actualizar el producto en la colección "productos"
            db.collection("productos")
                .document(producto.producto_id)
                .set(producto)
                .await()
            // Si tiene éxito, retorna Result.Success
            Result.Success(Unit)
        } catch (e: Exception) {
            // Si ocurre un error, retorna Result.Error con el mensaje
            Result.Error(e.message ?: "Error al actualizar producto")
        }
    }

    // Obtiene un producto por su ID
    suspend fun obtenerProductoPorId(productoId: String): Producto? {
        return try {
            val doc = db.collection("productos").document(productoId).get().await()
            doc.toObject(Producto::class.java)?.copy(producto_id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
} 