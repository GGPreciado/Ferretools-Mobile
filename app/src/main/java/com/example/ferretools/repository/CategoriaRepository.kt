package com.example.ferretools.repository

import android.util.Log
import com.example.ferretools.model.database.Categoria
import com.example.ferretools.model.Result
import com.example.ferretools.utils.SesionUsuario
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
    fun getCategoriasStream(): Flow<Result<List<Categoria>>> = callbackFlow {
        val negocioId = SesionUsuario.usuario?.negocioId
        if (negocioId.isNullOrEmpty()) {
            trySend(Result.Error("No hay sesión de usuario o negocio activo"))
            close() // Cierra el flujo inmediatamente
            return@callbackFlow
        }
        val listener = db.collection("categorias")
            .whereEqualTo("negocioId", negocioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val categorias = snapshot.documents.mapNotNull {
                        it.toObject(Categoria::class.java)?.copy(id = it.id)
                    }
                    trySend(Result.Success(categorias))
                }
            }
        awaitClose { listener.remove() }
    }

    // Agrega una nueva categoría a Firestore de forma suspendida
    suspend fun agregarCategoria(nombre: String): Result<Unit> {
        return try {
            // Crea un objeto Categoria con el nombre proporcionado
            val categoria = Categoria(
                nombre = nombre,
                negocioId = SesionUsuario.usuario?.negocioId!!
            )
            // Intenta agregar la categoría a la colección "categorias"
            val documentReference = db.collection("categorias").document()
            val categoriaParaGuardar = categoria.copy(id = documentReference.id)
            documentReference.set(categoriaParaGuardar).await()
            // Si tiene éxito, retorna Result.Success
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.d("DEBUG", "${e::class.simpleName}")
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

    // Obtiene una categoría por su ID
    suspend fun obtenerCategoriaPorId(categoriaId: String): Categoria? {
        return try {
            val doc = db.collection("categorias").document(categoriaId).get().await()
            doc.toObject(Categoria::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
} 