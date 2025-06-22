package com.example.ferretools.model.database

import com.example.ferretools.model.enums.MetodosPago
import com.google.firebase.Timestamp

data class Venta(
    val fecha: Timestamp? = null,
    val total: Double? = 0.0,
    val metodo_pago: MetodosPago = MetodosPago.Efectivo,
    val lista_productos: List<ItemUnitario> = emptyList(),
    val negocio_id: String? = null,
    val cliente_id: String? = null
)


