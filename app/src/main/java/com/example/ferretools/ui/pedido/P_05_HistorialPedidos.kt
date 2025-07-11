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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ferretools.viewmodel.pedido.PedidoViewModel
import com.example.ferretools.model.database.Pedido
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.ferretools.ui.home.HomeViewModel
import androidx.compose.ui.res.stringResource
import com.example.ferretools.R

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
                    stringResource(R.string.pedido_num, pedido.id),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(R.string.pedido_fecha_label, pedido.fecha),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
                Text(
                    stringResource(R.string.pedido_total_label, pedido.total),
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
            text = stringResource(R.string.pedido_historial),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (pedidos.isEmpty()) {
            Text(
                text = stringResource(R.string.pedido_no_historial),
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
    navController: NavController,
    pedidosHistorial: List<PedidoCliente> = emptyList(),
    selectedMenu: Int,
    onMenuSelect: (Int) -> Unit,
    onPedidoClick: (PedidoCliente) -> Unit,
    viewModel: PedidoViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    // Observar historial de pedidos en tiempo real
    val historialPedidos = viewModel.historialPedidos.collectAsState().value
    
    // Observar datos del usuario y negocio
    val userName = homeViewModel.userName.collectAsState().value
    val storeName = homeViewModel.storeName.collectAsState().value
    
    LaunchedEffect(Unit) {
        viewModel.cargarHistorialPedidosCliente()
    }
    Scaffold(
        topBar = { ClienteHeader(userName, storeName) },
        bottomBar = {
            ClienteBottomNavBar(selected = selectedMenu, onSelect = {
                when (it) {
                    0 -> navController.navigate(AppRoutes.Client.DASHBOARD)
                    1 -> navController.navigate(AppRoutes.Client.CATALOG)
                    2 -> navController.navigate(AppRoutes.Client.ORDERS)
                    3 -> navController.navigate(AppRoutes.Client.CONFIG)
                }
            })
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            var expanded by remember { mutableStateOf(false) }
            var ordenDescendente by remember { mutableStateOf(true) }
            val ordenLabel = if (ordenDescendente) stringResource(R.string.pedido_mas_recientes) else stringResource(R.string.pedido_mas_antiguos)
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box {
                        Button(onClick = { expanded = true }) {
                            Text(ordenLabel)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.pedido_mas_recientes)) },
                                onClick = {
                                    ordenDescendente = true
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.pedido_mas_antiguos)) },
                                onClick = {
                                    ordenDescendente = false
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                val pedidosOrdenados = if (ordenDescendente)
                    historialPedidos.sortedByDescending { it.fecha }
                else
                    historialPedidos.sortedBy { it.fecha }
                ListaHistorialPedidosCliente(
                    pedidos = pedidosOrdenados.map {
                        val fechaFormateada = it.fecha?.toDate()?.let { date ->
                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date)
                        } ?: ""
                        PedidoCliente(
                            id = it.pedidoId,
                            fecha = fechaFormateada,
                            estado = it.estado.replaceFirstChar { c -> c.uppercase() },
                            total = "S/ ${"%.2f".format(it.total ?: 0.0)}"
                        )
                    },
                    onPedidoClick = { pedido ->
                        navController.navigate(AppRoutes.Order.detailsWithId(pedido.id))
                    }
                )
            }
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
        navController = navController,
        pedidosHistorial = pedidosDemo,
        selectedMenu = 0,
        onMenuSelect = {},
        onPedidoClick = {}
    )
}