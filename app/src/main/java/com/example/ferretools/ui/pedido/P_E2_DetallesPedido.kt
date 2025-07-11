package com.example.ferretools.ui.pedido

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.viewmodel.pedido.PedidoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ferretools.model.database.Pedido
import com.example.ferretools.model.database.Producto
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.utils.SesionUsuario
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import android.util.Log
import androidx.compose.ui.res.stringResource
import com.example.ferretools.R

private val GreenPrimary = Color(0xFF22D366)
private val GreenSuccess = Color(0xFF00BF59)
private val RedError = Color.Red
private val BackgroundColor = Color(0xFFF8F8F8)
private val TextPrimary = Color(0xFF333333)
private val TextGray = Color.Gray

data class PedidoDetalle(
    val id: String,
    val cliente: String,
    val fecha: String,
    val productos: List<String>,
    val estado: String // "Pendiente", "Preparado", "Entregado", etc.
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P_E2_DetallesPedido(
    navController: NavController,
    pedidoId: String,
    viewModel: PedidoViewModel = viewModel(),
    onPrepararPedido: (() -> Unit)? = null,
    onPedidoCancelado: (() -> Unit)? = null
) {
    // Cargar historial y buscar el pedido
    val usuario = SesionUsuario.usuario
    val esCliente = usuario?.rol == RolUsuario.CLIENTE
    val esAlmacenero = usuario?.rol == RolUsuario.ALMACENERO

    val pedidos = if (esCliente)
        viewModel.historialPedidos.collectAsState().value
    else
        viewModel.todosPedidosNegocio.collectAsState().value

    val pedido = pedidos.find { it.pedidoId == pedidoId }

    LaunchedEffect(esCliente) {
        if (esCliente) viewModel.cargarHistorialPedidosCliente()
        else viewModel.cargarTodosPedidosNegocio()
    }
    // Obtener productos por ID
    val productoIds: List<String> = pedido?.lista_productos?.mapNotNull { it.producto_id } ?: emptyList()
    LaunchedEffect(productoIds) {
        if (productoIds.isNotEmpty()) viewModel.getProductosPorIds(productoIds)
    }
    val productosPorId: Map<String, Producto?> = viewModel.productosPorId.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Pedido") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.LocalShipping, contentDescription = "Atrás", tint = Color.Black)
                    }
                }
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        stringResource(R.string.pedido_num, pedido?.pedidoId ?: "-"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.pedido_cliente, pedido?.clienteId ?: "-"), style = MaterialTheme.typography.bodyLarge)
                    val fechaFormateada = pedido?.fecha?.toDate()?.toString() ?: "-"
                    Text(stringResource(R.string.pedido_fecha_label, fechaFormateada), style = MaterialTheme.typography.bodyMedium, color = TextGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.pedido_productos), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    pedido?.lista_productos?.forEach { item ->
                        val producto = productosPorId[item.producto_id]
                        val nombre = producto?.nombre ?: stringResource(R.string.pedido_producto_desconocido)
                        val cantidad = item.cantidad ?: 0
                        Text("- $nombre x$cantidad", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = when (pedido?.estado?.lowercase()) {
                                "preparado", "entregado" -> GreenSuccess
                                "cancelado" -> RedError
                                else -> TextGray
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.pedido_estado, pedido?.estado ?: "-"),
                            style = MaterialTheme.typography.bodyLarge,
                            color = when (pedido?.estado?.lowercase()) {
                                "preparado", "entregado" -> GreenSuccess
                                "cancelado" -> RedError
                                else -> TextGray
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            // Botón para preparar/cancelar pedido según rol y estado
            if (pedido != null && pedido.estado.lowercase() == "pendiente") {
                Spacer(modifier = Modifier.height(32.dp))
                if (esAlmacenero) {
                    Button(
                        onClick = {
                            navController.navigate(AppRoutes.Order.Employee.prepareWithId(pedido.pedidoId))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.pedido_preparar), style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }
                } else if (esCliente) {
                    Button(
                        onClick = {
                            Log.d("P_E2_DetallesPedido", "Cliente cancela pedido: ${pedido.pedidoId}")
                            viewModel.cancelarPedido(pedido.pedidoId)
                            onPedidoCancelado?.invoke()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedError),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.pedido_cancelar), style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewP_E2_DetallesPedido() {
    val navController = rememberNavController()
    val pedidoDemo = PedidoDetalle(
        id = "003",
        cliente = "Luis Torres",
        fecha = "2024-06-12",
        productos = listOf("Aceite x1", "Fideos x2", "Gaseosa x1"),
        estado = "Pendiente"
    )
    P_E2_DetallesPedido(
        navController = navController,
        pedidoId = "003", // Assuming a valid ID for the preview
        viewModel = viewModel(), // Pass a dummy viewModel for preview
        onPrepararPedido = {},
        onPedidoCancelado = {}
    )
}