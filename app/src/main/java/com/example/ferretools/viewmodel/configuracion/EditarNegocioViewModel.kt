package com.example.ferretools.viewmodel.configuracion

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ferretools.model.database.Negocio
import com.example.ferretools.model.states.configuracion.EditarNegocioUiState
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditarNegocioViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(EditarNegocioUiState())
    val uiState = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    init {
        cargarDatosNegocio()
    }

    private fun updateState(trasform: (EditarNegocioUiState) -> EditarNegocioUiState) {
        _uiState.update { current ->
            val updated = trasform(current)

            updated.copy(
                formsValido = verficarCampos(updated)
            )
        }
    }

    fun actualizarNombreNegocio(nombre: String) {
        updateState {
            it.copy(
                nombreNegocio = nombre,
                errorNombre = verificarErrorNombre(nombre)
            )
        }
    }

    fun actualizarTipoNegocio(tipo: String) {
        updateState {
            it.copy(
                tipoNegocio = tipo,
                errorTipo = verificarErrorTipo(tipo)
            )
        }
    }

    fun actualizarDireccionNegocio(direccion: String) {
        updateState {
            it.copy(
                direccionNegocio = direccion,
                errorDireccion = verificarErrorDireccion(direccion)
            )
        }
    }

    fun actualizarRuc(ruc: String) {
        updateState {
            it.copy(
                ruc = ruc,
                errorRuc = verificarErrorRuc(ruc)
            )
        }
    }

    fun actualizarFotoUri(fotoUri: Uri?) {
        updateState {
            it.copy(fotoLocalUri = fotoUri)
        }
    }

    private fun cargarDatosNegocio() {
        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("negocios")
                .whereEqualTo("gerenteId", uid)
                .limit(1)
                .get()
                .addOnSuccessListener { documento ->
                    val negocio = documento.documents.firstOrNull()?.toObject(Negocio::class.java)
                    _uiState.update {
                        it.copy(
                            nombreNegocio = negocio!!.nombre,
                            tipoNegocio = negocio.tipo,
                            direccionNegocio = negocio.direccion,
                            ruc = negocio.ruc,
                            fotoRemotaUrl = negocio.logoUrl
                        )
                    }

                }
                .addOnFailureListener {
                    Log.e("DEBUG", "Error al cargar negocio: ${it.message}")
                }
        }
    }

    private fun verificarErrorNombre(nombre: String): String? {
        if (nombre.isBlank()) {
            return "Debe rellenar este campo"
        } else {
            return null
        }
    }

    private fun verificarErrorTipo(tipo: String): String? {
        if (tipo.isBlank()) {
            return "Debe rellenar este campo"
        } else {
            return null
        }
    }

    private fun verificarErrorDireccion(direccion: String): String? {
        if (direccion.isBlank()) {
            return "Debe rellenar este campo"
        } else {
            return null
        }
    }

    private fun verificarErrorRuc(ruc: String): String? {
        if (ruc.isBlank()) {
            return "Debe rellenar este campo"
        } else {
            return null
        }
    }

    private fun verficarCampos(state: EditarNegocioUiState): Boolean {
        return listOf(
            state.nombreNegocio,
            state.tipoNegocio,
            state.direccionNegocio,
            state.ruc
        ).all { it.isNotBlank() }
    }

//    fun subirImagen(userId: String, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
//        val bytes = imagenBytes
//        if (bytes == null) {
//            onError(Exception("No hay imagen cargada"))
//            return
//        }
//
//        val imageRef = storage.reference.child("usuarios/$userId/negocio_logo.jpg")
//
//        imageRef.putBytes(bytes)
//            .continueWithTask { task ->
//                if (!task.isSuccessful) {
//                    throw task.exception ?: Exception("Error al subir imagen")
//                }
//                imageRef.downloadUrl
//            }
//            .addOnSuccessListener { downloadUrl ->
//                onSuccess(downloadUrl.toString())
//            }
//            .addOnFailureListener { exception ->
//                onError(exception)
//            }
//    }

    private fun subirImagen(uri: Uri, userId: String, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        val imageRef = storage.reference.child("usuarios/$userId/negocio_logo.jpg")

        imageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Error al subir imagen")
                }
                imageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                onSuccess(downloadUrl.toString())
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    private fun actualizarNegocio(uid: String, fotoUrl: String?) {
        val negocioActualizado = mapOf(
            "nombre" to _uiState.value.nombreNegocio,
            "tipo" to _uiState.value.tipoNegocio,
            "direccion" to _uiState.value.direccionNegocio,
            "ruc" to _uiState.value.ruc,
            "logoUrl" to fotoUrl
        )

        db.collection("negocios")
            .whereEqualTo("gerenteId", uid)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documento = querySnapshot.documents.first()
                    db.collection("negocios")
                        .document(documento.id)
                        .update(negocioActualizado)
                        .addOnSuccessListener {
                            Log.d("TAG", "Negocio actualizado")
                            _uiState.update { it.copy(edicionExitosa = true) }
                        }
                        .addOnFailureListener { e ->
                            Log.e("TAG", "Error al actualizar documento: ${e.message}")
                        }
                } else {
                    Log.e("TAG", "No se encontró un negocio con ese gerenteId")
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error al buscar negocio: ${e.message}")
            }
    }

    fun editarNegocio() {
        val uid = SesionUsuario.usuario!!.uid
        val fotoLocal = _uiState.value.fotoLocalUri
        val fotoRemote = _uiState.value.fotoRemotaUrl

        if (fotoLocal != null) {
            subirImagen(
                uri = fotoLocal,
                userId = uid,
                onSuccess = { fotoUrl ->
                    actualizarNegocio(uid, fotoUrl)
                },
                onError = { e ->
                    Log.e("TAG", "Error al subir imagen: ${e.message}")
                }
            )
        } else {
            actualizarNegocio(uid, fotoRemote)
        }
    }

}