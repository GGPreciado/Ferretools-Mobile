package com.example.ferretools.repository

import com.example.ferretools.model.database.Categoria
import com.example.ferretools.model.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// Repositorio para manejar operaciones de categorías en Firestore
class CategoriaRepository(
    // Instancia de la base de datos Firestore (por defecto, la instancia global)
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // Obtiene un flujo (Flow) en tiempo real de la lista de categorías
    fun getCategoriasStream(): Flow<Result<List<Categoria>>> = callbackFlow {
        // Listener para escuchar cambios en la colección "categorias"
        val listener = db.collection("categorias")
            .addSnapshotListener { snapshot, error ->
                // Si ocurre un error, lo envía al flujo como Result.Error
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }
                // Si hay datos, los convierte a objetos Categoria y los envía como Result.Success
                if (snapshot != null) {
                    val categorias = snapshot.documents.mapNotNull { it.toObject(Categoria::class.java) }
                    trySend(Result.Success(categorias))
                }
            }
        // Cuando se cierra el flujo, elimina el listener para evitar fugas de memoria
        awaitClose { listener.remove() }
    }

    // Agrega una nueva categoría a Firestore de forma suspendida
    suspend fun agregarCategoria(nombre: String): Result<Unit> {
        return try {
            // Crea un objeto Categoria con el nombre proporcionado
            val categoria = Categoria(nombre = nombre)
            // Intenta agregar la categoría a la colección "categorias"
            db.collection("categorias").add(categoria).await()
            // Si tiene éxito, retorna Result.Success
            Result.Success(Unit)
        } catch (e: Exception) {
            // Si ocurre un error, retorna Result.Error con el mensaje
            Result.Error(e.message ?: "Error al agregar categoría")
        }
    }

    // Edita el nombre de una categoría existente
    suspend fun editarCategoria(id: String, nuevoNombre: String): Boolean {
        return try {
            db.collection("categorias")
                .document(id)
                .update("nombre", nuevoNombre)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Elimina una categoría existente
    suspend fun eliminarCategoria(id: String): Boolean {
        return try {
            db.collection("categorias")
                .document(id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
} 