package com.example.ferretools.repository

import com.example.ferretools.model.database.Pedido
import com.example.ferretools.model.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

class PedidoRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Registra un pedido en la colección 'pedidos'.
     */
    suspend fun registrarPedido(pedido: Pedido): Result<String> {
        return try {
            val docRef = db.collection("pedidos").add(pedido).await()
            Log.d("PedidoRepository", "Pedido registrado: ${pedido}")
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Log.e("PedidoRepository", "Error al registrar pedido: ${e.message}")
            Result.Error(e.message ?: "Error al registrar el pedido")
        }
    }

    /**
     * Actualiza el estado de un pedido.
     */
    suspend fun actualizarEstadoPedido(pedidoId: String, nuevoEstado: String): Result<Unit> {
        return try {
            db.collection("pedidos").document(pedidoId).update("estado", nuevoEstado).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("PedidoRepository", "Error al actualizar estado: ${e.message}")
            Result.Error(e.message ?: "Error al actualizar el estado del pedido")
        }
    }
} 