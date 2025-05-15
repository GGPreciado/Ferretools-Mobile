package com.example.ferretools.ui.pedido

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.home.ClienteBottomNavBar
import com.example.ferretools.ui.home.ClienteHeader
import com.example.ferretools.ui.home.PedidoCliente

// --- Constantes de Estilo ---
private val GreenPrimary = Color(0xFF22D366)
private val GreenSecondary = Color(0xFF00BF59)
private val OrangeStatus = Color(0xFFE65100)
private val BackgroundColor = Color(0xFFF8F8F8)
private val TextPrimary = Color(0xFF333333)
private val TextGray = Color.Gray
private val CardElevation = 2.dp

/*
@Composable
fun ClienteHistorialTopBar(navController: NavController, text: String) {
    TopAppBar(
        title = {
            Text(text = text, color = Color.Black, fontSize = 16.sp)
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.Black
                )
            }
        },
        backgroundColor = GreenPrimary,
        elevation = 0.dp
    )
}*/

@Composable
fun PedidoClienteHistorialCard(
    pedido: PedidoCliente,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Pedido #${pedido.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Fecha: ${pedido.fecha}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
                Text(
                    "Total: ${pedido.total}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = pedido.estado,
                color = when (pedido.estado) {
                    "Entregado" -> GreenPrimary
                    "Listo" -> GreenSecondary
                    else -> OrangeStatus
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ListaHistorialPedidosCliente(
    pedidos: List<PedidoCliente>,
    onPedidoClick: (PedidoCliente) -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Historial de Pedidos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (pedidos.isEmpty()) {
            Text(
                text = "No hay pedidos en el historial.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextGray,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn {
                items(pedidos) { pedido ->
                    PedidoClienteHistorialCard(pedido = pedido) {
                        onPedidoClick(pedido)
                    }
                }
            }
        }
    }
}

@Composable
fun P_05_HistorialPedidos(
    userName: String,
    storeName: String,
    navController: NavController,
    pedidosHistorial: List<PedidoCliente>,
    selectedMenu: Int,
    onMenuSelect: (Int) -> Unit,
    onPedidoClick: (PedidoCliente) -> Unit
    // viewModel: HistorialPedidosViewModel = viewModel() // Para uso futuro
) {
    Scaffold(
        topBar = { ClienteHeader(userName, storeName) },
        bottomBar = { ClienteBottomNavBar(selected = selectedMenu, onSelect = onMenuSelect) },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ListaHistorialPedidosCliente(
                pedidos = pedidosHistorial,
                onPedidoClick = { pedido ->
                    onPedidoClick(pedido)
                    navController.navigate(AppRoutes.Order.RECEIPT)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun P_05_HistorialPedidosPreview() {
    val navController = rememberNavController()
    val pedidosDemo = listOf(
        PedidoCliente("1001", "2024-06-10", "Entregado", "S/ 45.00"),
        PedidoCliente("1002", "2024-06-12", "Listo", "S/ 30.00"),
        PedidoCliente("1003", "2024-06-13", "En preparación", "S/ 20.00")
    )

    P_05_HistorialPedidos(
        userName = "Carlos Ruiz",
        storeName = "Bodega Central",
        navController = navController,
        pedidosHistorial = pedidosDemo,
        selectedMenu = 0,
        onMenuSelect = {},
        onPedidoClick = {}
    )
}