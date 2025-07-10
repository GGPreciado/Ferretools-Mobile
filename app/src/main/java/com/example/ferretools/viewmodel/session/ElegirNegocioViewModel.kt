package com.example.ferretools.viewmodel.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Negocio
import com.example.ferretools.model.database.Solicitud
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.repository.NegocioRepository
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class NegociosUiState {
    object Loading : NegociosUiState()
    data class Success(val negocios: List<Negocio>) : NegociosUiState()
    data class Error(val message: String) : NegociosUiState()
}

class ElegirNegocioViewModel(
    private val negocioRepo: NegocioRepository = NegocioRepository()
) : ViewModel() {
    private val _negociosState = MutableStateFlow<NegociosUiState>(NegociosUiState.Loading)
    val negociosState = _negociosState.asStateFlow()

    private val auth = Firebase.auth

    init {
        cargarNegocios()
    }

    fun cargarNegocios() {
        viewModelScope.launch {
            _negociosState.value = NegociosUiState.Loading
            negocioRepo.getNegociosStream().collect { result ->
                when (result) {
                    is com.example.ferretools.model.Result.Success -> {
                        _negociosState.value = NegociosUiState.Success(result.data)
                    }
                    is com.example.ferretools.model.Result.Error -> {
                        _negociosState.value = NegociosUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun afiliarUsuarioANegocio(negocioId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onError("No hay usuario autenticado")
            return
        }

        viewModelScope.launch {
            val result = negocioRepo.actualizarNegocioUsuario(user.uid, negocioId)
            when (result) {
                is com.example.ferretools.model.Result.Success -> {
                    // Actualizar la sesión local
                    SesionUsuario.actualizarDatos(negocioId = negocioId)
                    onSuccess()
                }
                is com.example.ferretools.model.Result.Error -> {
                    onError(result.message)
                }
            }
        }
    }

    fun crearSolicitudEmpleo(negocioId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
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
            "fotoUri" to (usuario.fotoUrl?.toString() ?: ""),
            "rolSolicitado" to "ALMACENERO",
            "estado" to "pendiente",
            "negocioId" to negocioId
        )
        val db = Firebase.firestore
        db.collection("solicitudes")
            .add(solicitudMap)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al crear solicitud")
            }
    }

    fun flujoAfiliacionONuevoNegocio(
        negocioId: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val rolDeseado = SesionUsuario.rolDeseado
        val user = auth.currentUser
        Log.e("DEBUG", "Inicia flujoAfiliacionONuevoNegocio: negocioId=$negocioId, rolDeseado=$rolDeseado, user=${user?.uid}")
        if (user == null) {
            Log.e("DEBUG", "No hay usuario autenticado")
            onError("No hay usuario autenticado")
            return
        }
        if (rolDeseado == RolUsuario.ADMIN && negocioId != null) {
            Log.e("DEBUG", "Afiliando como cliente y creando solicitud para ADMIN en negocioId=$negocioId")
            afiliarUsuarioANegocio(
                negocioId = negocioId,
                onSuccess = {
                    Log.e("DEBUG", "Afiliación exitosa como cliente para ADMIN en negocioId=$negocioId")
                    crearSolicitudParaRol(
                        negocioId = negocioId,
                        rolSolicitado = RolUsuario.ADMIN,
                        onSuccess = {
                            Log.e("DEBUG", "Solicitud para ADMIN creada exitosamente en negocioId=$negocioId")
                            onSuccess()
                        },
                        onError = {
                            Log.e("DEBUG", "Error al crear solicitud para ADMIN: $it")
                            onError(it)
                        }
                    )
                },
                onError = {
                    Log.e("DEBUG", "Error al afiliar como cliente para ADMIN: $it")
                    onError(it)
                }
            )
        } else if (rolDeseado == RolUsuario.ALMACENERO && negocioId != null) {
            Log.e("DEBUG", "Afiliando como cliente y creando solicitud para ALMACENERO en negocioId=$negocioId")
            afiliarUsuarioANegocio(
                negocioId = negocioId,
                onSuccess = {
                    Log.e("DEBUG", "Afiliación exitosa como cliente para ALMACENERO en negocioId=$negocioId")
                    crearSolicitudParaRol(
                        negocioId = negocioId,
                        rolSolicitado = RolUsuario.ALMACENERO,
                        onSuccess = {
                            Log.e("DEBUG", "Solicitud para ALMACENERO creada exitosamente en negocioId=$negocioId")
                            onSuccess()
                        },
                        onError = {
                            Log.e("DEBUG", "Error al crear solicitud para ALMACENERO: $it")
                            onError(it)
                        }
                    )
                },
                onError = {
                    Log.e("DEBUG", "Error al afiliar como cliente para ALMACENERO: $it")
                    onError(it)
                }
            )
        } else if (rolDeseado == RolUsuario.CLIENTE && negocioId != null) {
            Log.e("DEBUG", "Afiliando como cliente en negocioId=$negocioId")
            afiliarUsuarioANegocio(
                negocioId = negocioId,
                onSuccess = {
                    Log.e("DEBUG", "Afiliación exitosa como cliente en negocioId=$negocioId")
                    onSuccess()
                },
                onError = {
                    Log.e("DEBUG", "Error al afiliar como cliente: $it")
                    onError(it)
                }
            )
        } else {
            Log.e("DEBUG", "Opción no válida en flujoAfiliacionONuevoNegocio")
            onError("Opción no válida")
        }
    }

    private fun crearSolicitudParaRol(
        negocioId: String,
        rolSolicitado: RolUsuario,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val usuario = SesionUsuario.usuario
        Log.e("DEBUG", "Inicia crearSolicitudParaRol: negocioId=$negocioId, rolSolicitado=$rolSolicitado, usuario=${usuario?.uid}")
        if (usuario == null) {
            Log.e("DEBUG", "No hay usuario en sesión para crear solicitud")
            onError("No hay usuario en sesión")
            return
        }
        val solicitudMap = mapOf(
            "usuarioId" to usuario.uid,
            "nombreUsuario" to usuario.nombre,
            "correo" to usuario.correo,
            "celular" to usuario.celular,
            "fotoUri" to (usuario.fotoUrl?.toString() ?: ""),
            "rolSolicitado" to rolSolicitado.name,
            "estado" to "pendiente",
            "negocioId" to negocioId
        )
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        db.collection("solicitudes")
            .add(solicitudMap)
            .addOnSuccessListener {
                Log.e("DEBUG", "Solicitud creada exitosamente en Firestore: $solicitudMap")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG", "Error al crear solicitud en Firestore: ${e.message}")
                onError(e.message ?: "Error al crear solicitud")
            }
    }
} 