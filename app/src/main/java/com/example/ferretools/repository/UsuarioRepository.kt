package com.example.ferretools.repository

import android.util.Log
import androidx.compose.animation.core.snap
import com.example.ferretools.model.Result
import com.example.ferretools.model.database.Usuario
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UsuarioRepository(
    private val db: FirebaseFirestore = Firebase.firestore
) {
    suspend fun obtenerUsuarioPorId(id: String): Usuario? {
        try {
            val snapshot = db.collection("usuarios")
                .document(id)
                .get()
                .await()

            val usuario = snapshot.toObject(Usuario::class.java)

            if (usuario != null) {
                return usuario
            } else {
                throw Exception("No se encontró el usuario con id: $id")
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error al obtener usuario: ${e.message}")
            return null
        }
    }
}