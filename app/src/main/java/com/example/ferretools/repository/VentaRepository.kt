package com.example.ferretools.repository

import com.example.ferretools.model.database.Venta
import com.example.ferretools.model.database.ItemUnitario
import com.example.ferretools.model.database.Producto
import com.example.ferretools.model.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

class VentaRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Registra una venta en la colección 'ventas' y actualiza el stock de los productos vendidos.
     */
    suspend fun registrarVenta(venta: Venta): Result<Unit> {
        return try {
            // Registrar la venta en la colección "ventas"
            db.collection("ventas").add(venta).await()
            Log.d("VentaRepository", "Venta registrada: ${venta}")
            // Actualizar el stock de cada producto vendido (disminuir)
            venta.lista_productos.forEach { item ->
                item.producto_id?.let { productoId ->
                    Log.d("VentaRepository", "Actualizando stock para producto: $productoId, cantidad vendida: ${item.cantidad}")
                    actualizarStockProducto(productoId, item.cantidad ?: 0)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("VentaRepository", "Error al registrar venta: ${e.message}")
            Result.Error(e.message ?: "Error al registrar la venta")
        }
    }

    /**
     * Actualiza el stock (cantidad_disponible) de un producto restando la cantidad vendida.
     */
    private suspend fun actualizarStockProducto(productoId: String, cantidadVendida: Int) {
        val productoRef = db.collection("productos").document(productoId)
        val snapshot = productoRef.get().await()
        val producto = snapshot.toObject(Producto::class.java)
        producto?.let {
            val nuevoStock = (it.cantidad_disponible) - cantidadVendida
            if (nuevoStock >= 0) {
                Log.d("VentaRepository", "Nuevo stock para $productoId: $nuevoStock")
                productoRef.update("cantidad_disponible", nuevoStock).await()
            } else {
                Log.w("VentaRepository", "Stock insuficiente para $productoId. Stock actual: ${it.cantidad_disponible}, intentando vender: $cantidadVendida")
                throw Exception("Stock insuficiente para el producto")
            }
        }
    }

    /**
     * Obtiene todas las ventas de un negocio específico.
     */
    suspend fun obtenerVentasPorNegocio(negocioId: String): Result<List<Venta>> {
        return try {
            val snapshot = db.collection("ventas")
                .whereEqualTo("negocioId", negocioId)
                .get()
                .await()
            
            val ventas = snapshot.documents.mapNotNull { it.toObject(Venta::class.java) }
            Result.Success(ventas)
        } catch (e: Exception) {
            Log.e("VentaRepository", "Error al obtener ventas: ${e.message}")
            Result.Error(e.message ?: "Error al obtener las ventas")
        }
    }
} 