package com.example.ferretools.model.database

import com.example.ferretools.model.enums.MetodosPago
import com.google.firebase.Timestamp

/**
 * Modelo de datos para un Pedido realizado por un cliente.
 */
data class Pedido(
    val pedidoId: String = "", // ID del documento Firestore
    val fecha: Timestamp? = null,
    val total: Double? = 0.0,
    val metodo_pago: MetodosPago = MetodosPago.Efectivo,
    val lista_productos: List<ItemUnitario> = emptyList(),
    val negocioId: String? = null,
    val clienteId: String? = null,
    val estado: String = "pendiente" // pendiente, aceptado, entregado, cancelado
) 