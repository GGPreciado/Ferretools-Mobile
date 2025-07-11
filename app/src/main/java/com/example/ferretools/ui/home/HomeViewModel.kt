package com.example.ferretools.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Pedido
import com.example.ferretools.repository.PedidoRepository
import com.example.ferretools.repository.NegocioRepository
import com.example.ferretools.utils.SesionUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel : ViewModel() {
    private val pedidoRepository = PedidoRepository()
    private val negocioRepository = NegocioRepository()
    
    private val _pedidosPendientes = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidosPendientes: StateFlow<List<Pedido>> = _pedidosPendientes.asStateFlow()

    // Estados para el nombre de usuario y negocio
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()
    
    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName.asStateFlow()

    private var pedidosListener: ListenerRegistration? = null

    init {
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        val usuario = SesionUsuario.usuario
        if (usuario != null) {
            _userName.value = usuario.nombre
            cargarNombreNegocio(usuario.negocioId)
        }
    }

    private fun cargarNombreNegocio(negocioId: String?) {
        if (negocioId.isNullOrEmpty()) {
            _storeName.value = "Sin negocio asignado"
            return
        }
        
        viewModelScope.launch {
            try {
                val negocio = negocioRepository.obtenerNegocioPorId(negocioId)
                _storeName.value = negocio?.nombre ?: "Negocio no encontrado"
            } catch (e: Exception) {
                _storeName.value = "Error al cargar negocio"
            }
        }
    }

    fun cargarPedidosPendientes() {
        val usuario = SesionUsuario.usuario
        if (usuario?.uid.isNullOrEmpty()) return
        // Remover listener anterior si existe
        pedidosListener?.remove()
        pedidosListener = pedidoRepository.db.collection("pedidos")
            .whereEqualTo("clienteId", usuario.uid)
            .whereEqualTo("estado", "pendiente")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    _pedidosPendientes.value = emptyList()
                    return@addSnapshotListener
                }
                val pedidos = snapshot.documents.mapNotNull { it.toObject(Pedido::class.java)?.copy(pedidoId = it.id) }
                _pedidosPendientes.value = pedidos
            }
    }

    override fun onCleared() {
        super.onCleared()
        pedidosListener?.remove()
    }
} 