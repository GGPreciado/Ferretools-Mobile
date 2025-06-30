package com.example.ferretools.viewmodel.configuracion

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.database.Solicitud
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class MiSolicitudUiState {
    object Loading : MiSolicitudUiState()
    data class Success(val solicitud: Solicitud) : MiSolicitudUiState()
    object Empty : MiSolicitudUiState()
    data class Error(val message: String) : MiSolicitudUiState()
}

class MiSolicitudViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<MiSolicitudUiState>(MiSolicitudUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val db = Firebase.firestore

    init {
        fetchMiSolicitud()
    }

    fun fetchMiSolicitud() {
        val usuario = SesionUsuario.usuario
        if (usuario == null) {
            _uiState.value = MiSolicitudUiState.Error("No hay usuario en sesión")
            return
        }
        db.collection("solicitudes")
            .whereEqualTo("usuarioId", usuario.uid)
            .whereEqualTo("rolSolicitado", "ALMACENERO")
            .whereEqualTo("estado", "pendiente")
            .get()
            .addOnSuccessListener { result ->
                val doc = result.documents.firstOrNull()
                if (doc != null) {
                    val id = doc.id
                    val usuarioId = doc.getString("usuarioId") ?: ""
                    val nombreUsuario = doc.getString("nombreUsuario") ?: ""
                    val correo = doc.getString("correo") ?: ""
                    val celular = doc.getString("celular") ?: ""
                    val fotoUriString = doc.getString("fotoUri")
                    val fotoUri = fotoUriString?.let { Uri.parse(it) }
                    val rolSolicitado = RolUsuario.ALMACENERO
                    val estado = doc.getString("estado") ?: "pendiente"
                    val solicitud = Solicitud(id, usuarioId, nombreUsuario, correo, celular, fotoUri, rolSolicitado, estado)
                    _uiState.value = MiSolicitudUiState.Success(solicitud)
                } else {
                    _uiState.value = MiSolicitudUiState.Empty
                }
            }
            .addOnFailureListener { e ->
                _uiState.value = MiSolicitudUiState.Error(e.message ?: "Error al cargar solicitud")
            }
    }

    fun cancelarSolicitud(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        if (state is MiSolicitudUiState.Success) {
            db.collection("solicitudes").document(state.solicitud.id)
                .delete()
                .addOnSuccessListener {
                    _uiState.value = MiSolicitudUiState.Empty
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e.message ?: "Error al cancelar solicitud")
                }
        }
    }

    fun crearSolicitud(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val usuario = SesionUsuario.usuario
        if (usuario == null) {
            onError("No hay usuario en sesión")
            return
        }
        val solicitudMap = mapOf(
            "usuarioId" to usuario.uid,
            "nombreUsuario" to usuario.nombre,
            "correo" to usuario.correo,
            "celular" to usuario.celular,
            "fotoUrl" to (usuario.fotoUrl?.toString() ?: ""),
            "rolSolicitado" to "ALMACENERO",
            "estado" to "pendiente"
        )
        db.collection("solicitudes")
            .add(solicitudMap)
            .addOnSuccessListener {
                // Enviar notificación push a los administradores
                enviarNotificacionPushAdmin()
                fetchMiSolicitud()
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al crear solicitud")
            }
    }

    private fun enviarNotificacionPushAdmin() {
        // En una implementación real, esto se haría con Cloud Functions
        // Por ahora, simulamos el envío de notificación
        // TODO: Implementar Cloud Function que envíe notificación push a todos los admins
        println("Notificación push enviada a administradores")
    }
} 