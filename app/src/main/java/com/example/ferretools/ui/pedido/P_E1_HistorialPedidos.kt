package com.example.ferretools.ui.pedido

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.home.EmpleadoBottomNavBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.ferretools.viewmodel.pedido.PedidoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

// --- Constantes de Estilo ---
private val GreenPrimary = Color(0xFF22D366)
private val GreenSuccess = Color(0xFF00BF59)
private val RedError = Color.Red
private val BackgroundColor = Color(0xFFF8F8F8)
private val TextPrimary = Color(0xFF333333)
private val TextGray = Color.Gray
private val CardElevation = 2.dp

// --- DATA CLASS ---
data class PedidoHistorial(
    val id: String,
    val cliente: String,
    val fecha: String,
    val productos: List<String>,
    val estado: String // Ej: "Entregado", "Cancelado"
)

// --- COMPONENTES UI ---
@Composable
fun AlmaceneroTopBar(
    navController: NavController,
    userName: String = "Nombre de Usuario",
    storeName: String = "Nombre de la Tienda",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .padding(top = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                userName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                storeName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun PedidoHistorialCard(
    pedido: PedidoHistorial,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Pedido #${pedido.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = pedido.estado,
                    color = when (pedido.estado) {
                        "Entregado" -> GreenSuccess
                        "Cancelado" -> RedError
                        else -> TextGray
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Cliente: ${pedido.cliente}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Fecha: ${pedido.fecha}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Productos:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            pedido.productos.forEach { producto ->
                Text(
                    "- $producto",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ListaHistorialPedidos(
    pedidos: List<PedidoHistorial>,
    onPedidoClick: (PedidoHistorial) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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
                    PedidoHistorialCard(pedido = pedido) { onPedidoClick(pedido) }
                }
            }
        }
    }
}

// --- PANTALLA PRINCIPAL ---
@Composable
fun P_E1_HistorialPedidos(
    navController: NavController,
    pedidosHistorial: List<PedidoHistorial> = emptyList(),
    onPedidoClick: (PedidoHistorial) -> Unit,
    userName: String = "Nombre de Usuario",
    storeName: String = "Nombre de la Tienda",
    viewModel: PedidoViewModel = viewModel()
) {
    // Observar todos los pedidos del negocio en tiempo real
    val todosPedidos = viewModel.todosPedidosNegocio.collectAsState().value
    LaunchedEffect(Unit) { viewModel.cargarTodosPedidosNegocio() }
    // DropdownMenu de orden
    var expanded by remember { mutableStateOf(false) }
    var ordenDescendente by remember { mutableStateOf(true) }
    val ordenLabel = if (ordenDescendente) "Más recientes primero" else "Más antiguos primero"
    Scaffold(
        topBar = { AlmaceneroTopBar(navController, userName, storeName) },
        bottomBar = { EmpleadoBottomNavBar(
            onInicio = { /* Pantalla actual */ },
            onInventario = { navController.navigate(AppRoutes.Employee.INVENTORY) },
            onHistorial = { navController.navigate(AppRoutes.Order.Employee.HISTORY) },
            onCuenta = { navController.navigate(AppRoutes.Config.MAIN) }) },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Historial de Pedidos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box {
                    Button(onClick = { expanded = true }, shape = RoundedCornerShape(24.dp)) {
                        Text(ordenLabel)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Más recientes primero") },
                            onClick = {
                                ordenDescendente = true
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Más antiguos primero") },
                            onClick = {
                                ordenDescendente = false
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            val pedidosOrdenados = if (ordenDescendente)
                todosPedidos.sortedByDescending { it.fecha }
            else
                todosPedidos.sortedBy { it.fecha }
            ListaHistorialPedidos(
                pedidos = pedidosOrdenados.map {
                    PedidoHistorial(
                        id = it.pedidoId,
                        cliente = it.clienteId ?: "-",
                        fecha = it.fecha?.toDate()?.let { date ->
                            java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(date)
                        } ?: "",
                        productos = it.lista_productos.map { item -> "${item.producto_id} x${item.cantidad}" },
                        estado = it.estado.replaceFirstChar { c -> c.uppercase() }
                    )
                },
                onPedidoClick = { pedido ->
                    navController.navigate(com.example.ferretools.navigation.AppRoutes.Order.DETAILS.replace("{pedidoId}", pedido.id))
                }
            )
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun PreviewP_E1_HistorialPedidos() {
    val navController = rememberNavController()
    val pedidosDemo = listOf(
        PedidoHistorial(
            id = "001",
            cliente = "Juan Pérez",
            fecha = "2024-06-10",
            productos = listOf("Arroz x2", "Azúcar x1"),
            estado = "Entregado"
        ),
        PedidoHistorial(
            id = "002",
            cliente = "María López",
            fecha = "2024-06-11",
            productos = listOf("Leche x3", "Pan x5"),
            estado = "Cancelado"
        )
    )
    P_E1_HistorialPedidos(
        navController = navController,
        pedidosHistorial = pedidosDemo,
        onPedidoClick = {},
        userName = "Carlos Ruiz",
        storeName = "Bodega Central"
    )
}