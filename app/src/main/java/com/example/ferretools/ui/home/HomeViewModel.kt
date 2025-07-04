package com.example.ferretools.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ferretools.model.database.Pedido
import com.example.ferretools.repository.PedidoRepository
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
    private val _pedidosPendientes = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidosPendientes: StateFlow<List<Pedido>> = _pedidosPendientes.asStateFlow()

    private var pedidosListener: ListenerRegistration? = null

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