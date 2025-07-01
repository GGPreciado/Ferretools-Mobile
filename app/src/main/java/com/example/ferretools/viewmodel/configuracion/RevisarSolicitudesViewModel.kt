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

sealed class SolicitudesUiState {
    object Loading : SolicitudesUiState()
    data class Success(val solicitudes: List<Solicitud>) : SolicitudesUiState()
    data class Error(val message: String) : SolicitudesUiState()
}

class RevisarSolicitudesViewModel : ViewModel() {
    private val _solicitudesState = MutableStateFlow<SolicitudesUiState>(SolicitudesUiState.Loading)
    val solicitudesState = _solicitudesState.asStateFlow()

    private val db = Firebase.firestore

    init {
        fetchSolicitudes()
    }

    fun fetchSolicitudes() {
        _solicitudesState.value = SolicitudesUiState.Loading
        val usuario = SesionUsuario.usuario
        val negocioId = usuario?.negocioId ?: ""
        db.collection("solicitudes")
            .whereEqualTo("estado", "pendiente")
            .whereEqualTo("negocioId", negocioId)
            .get()
            .addOnSuccessListener { result ->
                val solicitudes = result.documents.mapNotNull { doc ->
                    val id = doc.id
                    val usuarioId = doc.getString("usuarioId") ?: return@mapNotNull null
                    val nombreUsuario = doc.getString("nombreUsuario") ?: ""
                    val correo = doc.getString("correo") ?: ""
                    val celular = doc.getString("celular") ?: ""
                    val fotoUriString = doc.getString("fotoUri")
                    val fotoUri = fotoUriString?.let { Uri.parse(it) }
                    val rolSolicitadoStr = doc.getString("rolSolicitado") ?: "ALMACENERO"
                    val rolSolicitado = try { RolUsuario.valueOf(rolSolicitadoStr) } catch (e: Exception) { RolUsuario.ALMACENERO }
                    val estado = doc.getString("estado") ?: "pendiente"
                    val negocioId = doc.getString("negocioId") ?: ""
                    Solicitud(id, usuarioId, nombreUsuario, correo, celular, fotoUri, rolSolicitado, estado, negocioId)
                }
                _solicitudesState.value = SolicitudesUiState.Success(solicitudes)
            }
            .addOnFailureListener { e ->
                _solicitudesState.value = SolicitudesUiState.Error(e.message ?: "Error al cargar solicitudes")
            }
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