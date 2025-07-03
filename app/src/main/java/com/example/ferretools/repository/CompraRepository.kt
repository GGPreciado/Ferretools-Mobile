package com.example.ferretools.repository

import com.example.ferretools.model.database.Compra
import com.example.ferretools.model.database.ItemUnitario
import com.example.ferretools.model.database.Producto
import com.example.ferretools.model.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

class CompraRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Registra una compra en la colección 'compras' y actualiza el stock de los productos comprados.
     */
    suspend fun registrarCompra(compra: Compra): Result<Unit> {
        return try {
            // Registrar la compra en la colección "compras"
            db.collection("compras").add(compra).await()
            Log.d("CompraRepository", "Compra registrada: ${compra}")
            // Actualizar el stock de cada producto comprado
            compra.lista_productos.forEach { item ->
                item.producto_id?.let { productoId ->
                    Log.d("CompraRepository", "Actualizando stock para producto: $productoId, cantidad: ${item.cantidad}")
                    actualizarStockProducto(productoId, item.cantidad ?: 0)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("CompraRepository", "Error al registrar compra: ${e.message}")
            Result.Error(e.message ?: "Error al registrar la compra")
        }
    }

    /**
     * Actualiza el stock (cantidad_disponible) de un producto sumando la cantidad comprada.
     */
    private suspend fun actualizarStockProducto(productoId: String, cantidadComprada: Int) {
        val productoRef = db.collection("productos").document(productoId)
        val snapshot = productoRef.get().await()
        val producto = snapshot.toObject(Producto::class.java)
        producto?.let {
            val nuevoStock = (it.cantidad_disponible) + cantidadComprada
            Log.d("CompraRepository", "Nuevo stock para $productoId: $nuevoStock")
            productoRef.update("cantidad_disponible", nuevoStock).await()
        }
    }
} 