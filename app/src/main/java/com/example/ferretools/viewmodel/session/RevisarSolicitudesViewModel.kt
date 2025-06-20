package com.example.ferretools.viewmodel.session

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.database.Solicitud
import com.example.ferretools.model.enums.RolUsuario
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.ferretools.utils.NotificationHelper

sealed class SolicitudesUiState {
    object Loading : SolicitudesUiState()
    data class Success(val solicitudes: List<Solicitud>) : SolicitudesUiState()
    data class Error(val message: String) : SolicitudesUiState()
}

class RevisarSolicitudesViewModel : ViewModel() {
    private val _solicitudesState = MutableStateFlow<SolicitudesUiState>(SolicitudesUiState.Loading)
    val solicitudesState = _solicitudesState.asStateFlow()

    private val db = Firebase.firestore
    private var notificationHelper: NotificationHelper? = null

    init {
        fetchSolicitudes()
    }

    fun setContext(context: Context) {
        notificationHelper = NotificationHelper(context)
    }

    fun fetchSolicitudes() {
        _solicitudesState.value = SolicitudesUiState.Loading
        db.collection("solicitudes")
            .whereEqualTo("estado", "pendiente")
            .get()
            .addOnSuccessListener { result ->
                val solicitudes = result.documents.mapNotNull { doc ->
                    val id = doc.id
                    val usuarioId = doc.getString("usuarioId") ?: return@mapNotNull null
                    val nombreUsuario = doc.getString("nombreUsuario") ?: ""
                    val correo = doc.getString("correo") ?: ""
                    val celular = doc.getString("celular") ?: ""
                    val fotoUriString = doc.getString("fotoUri")
                    val fotoUri = fotoUriString?.let { android.net.Uri.parse(it) }
                    val rolSolicitadoStr = doc.getString("rolSolicitado") ?: "ALMACENERO"
                    val rolSolicitado = try { RolUsuario.valueOf(rolSolicitadoStr) } catch (e: Exception) { RolUsuario.ALMACENERO }
                    val estado = doc.getString("estado") ?: "pendiente"
                    Solicitud(id, usuarioId, nombreUsuario, correo, celular, fotoUri, rolSolicitado, estado)
                }
                _solicitudesState.value = SolicitudesUiState.Success(solicitudes)
                
                // Mostrar notificación local si hay solicitudes nuevas
                if (solicitudes.isNotEmpty()) {
                    mostrarNotificacionLocal(solicitudes.size)
                }
            }
            .addOnFailureListener { e ->
                _solicitudesState.value = SolicitudesUiState.Error(e.message ?: "Error al cargar solicitudes")
            }
    }

    private fun mostrarNotificacionLocal(cantidadSolicitudes: Int) {
        notificationHelper?.mostrarNotificacionSolicitud(
            "Nueva Solicitud de Empleo",
            "Tienes $cantidadSolicitudes solicitud(es) pendiente(s) de revisión"
        )
    }

    fun aceptarSolicitud(solicitud: Solicitud, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Cambiar el rol del usuario y actualizar la solicitud
        val usuarioRef = db.collection("usuarios").document(solicitud.usuarioId)
        val solicitudRef = db.collection("solicitudes").document(solicitud.id)
        usuarioRef.update("rol", solicitud.rolSolicitado.name)
            .addOnSuccessListener {
                solicitudRef.update("estado", "aceptada")
                    .addOnSuccessListener {
                        fetchSolicitudes()
                        onSuccess()
                    }
                    .addOnFailureListener { e -> onError(e.message ?: "Error al actualizar solicitud") }
            }
            .addOnFailureListener { e -> onError(e.message ?: "Error al actualizar usuario") }
    }

    fun rechazarSolicitud(solicitud: Solicitud, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val solicitudRef = db.collection("solicitudes").document(solicitud.id)
        solicitudRef.update("estado", "rechazada")
            .addOnSuccessListener {
                fetchSolicitudes()
                onSuccess()
            }
            .addOnFailureListener { e -> onError(e.message ?: "Error al rechazar solicitud") }
    }
} 