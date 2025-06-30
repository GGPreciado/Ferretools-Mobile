package com.example.ferretools.model.database

import com.example.ferretools.model.enums.MetodosPago
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Compra(
    val fecha: Timestamp,
    val total: Double,
    val metodo_pago: MetodosPago,
    val lista_productos: List<ItemUnitario>,
    val negocioId: DocumentReference,
    val proveedor_id: DocumentReference? = null
)