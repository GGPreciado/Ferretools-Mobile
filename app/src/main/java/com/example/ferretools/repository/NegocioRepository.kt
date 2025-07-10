package com.example.ferretools.repository

import com.example.ferretools.model.database.Negocio
import com.example.ferretools.model.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// Repositorio para manejar operaciones de negocios en Firestore
class NegocioRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // Obtener stream de todos los negocios disponibles
    fun getNegociosStream(): Flow<Result<List<Negocio>>> = callbackFlow {
        val listener = db.collection("negocios")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val negocios = snapshot.documents.mapNotNull { 
                        it.toObject(Negocio::class.java)?.copy(id = it.id)
                    }
                    trySend(Result.Success(negocios))
                }
            }
        awaitClose { listener.remove() }
    }

    // Obtener un negocio por ID
    suspend fun obtenerNegocioPorId(negocioId: String): Negocio? {
        return try {
            val doc = db.collection("negocios").document(negocioId).get().await()
            doc.toObject(Negocio::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // Actualizar el negocioId de un usuario
    suspend fun actualizarNegocioUsuario(usuarioId: String, negocioId: String): Result<Unit> {
        return try {
            db.collection("usuarios")
                .document(usuarioId)
                .update("negocioId", negocioId)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al actualizar negocio del usuario")
        }
    }

    // Crear un nuevo negocio
    suspend fun crearNegocio(negocio: Negocio): Result<String> {
        return try {
            val docRef = db.collection("negocios").document()
            val negocioConId = negocio.copy(id = docRef.id)
            docRef.set(negocioConId).await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al crear negocio")
        }
    }

    // Actualizar un negocio existente
    suspend fun actualizarNegocio(negocio: Negocio): Result<Unit> {
        return try {
            db.collection("negocios")
                .document(negocio.id)
                .set(negocio)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al actualizar negocio")
        }
    }
} 