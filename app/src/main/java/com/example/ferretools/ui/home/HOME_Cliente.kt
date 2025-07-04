package com.example.ferretools.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.ferretools.navigation.AppRoutes

private val GreenP = Color(0xFF22D366)
private val GreenDark = Color(0xFF00BF59)
private val Orange = Color(0xFFE65100)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color.Gray
private val BackgroundLight = Color(0xFFF8F8F8)

data class PedidoCliente(
    val id: String,
    val fecha: String,
    val estado: String,
    val total: String
)

@Composable
fun HOME_Cliente(
    navController: NavController,
    userName: String = "Cliente",
    storeName: String = "Mi Tienda",
    pedidosRecientes: List<PedidoCliente> = emptyList(),
    selectedMenu: Int = 0,
    // viewModel: HomeClienteViewModel = viewModel() // Para uso futuro
) {
    var menu by remember { mutableStateOf(selectedMenu) }

    Scaffold(
        topBar = { ClienteHeader(userName, storeName) },
        bottomBar = {
            ClienteBottomNavBar(selected = menu, onSelect = {
                menu = it
                when (it) {
                    1 -> navController.navigate(AppRoutes.Client.CATALOG)
                    2 -> navController.navigate(AppRoutes.Order.HISTORY)
                    3 -> navController.navigate(AppRoutes.Config.MAIN)
                    else -> {} // Inicio
                }
            })
        },
        backgroundColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                "¡Bienvenido, $userName!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            ClienteQuickAccess(
                onCatalogo = { navController.navigate(AppRoutes.Client.CATALOG) },
                onCarrito = { navController.navigate(AppRoutes.Order.ADD_TO_CART) },
                onHistorial = { navController.navigate(AppRoutes.Order.HISTORY) }
            )
            Spacer(Modifier.height(24.dp))
            PedidosRecientesCliente(
                pedidos = pedidosRecientes,
                onPedidoClick = { pedido ->
                    navController.navigate(AppRoutes.Order.RECEIPT) // Puedes pasar el ID como parámetro si lo necesitas
                }
            )
        }
    }
}

@Composable
fun ClienteHeader(userName: String, storeName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenP)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(userName, color = Color.White, fontWeight = FontWeight.Bold)
            Text(storeName, color = Color.White, fontSize = 13.sp)
        }
    }
}

@Composable
fun ClienteQuickAccess(
    onCatalogo: () -> Unit,
    onCarrito: () -> Unit,
    onHistorial: () -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text("Accesos Rápidos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickAccessButtonC("Catálogo", Icons.Default.List, onCatalogo)
            QuickAccessButtonC("Carrito", Icons.Default.ShoppingCart, onCarrito)
            QuickAccessButtonC("Historial", Icons.Default.History, onHistorial)
        }
    }
}

@Composable
private fun QuickAccessButtonC(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(GreenP, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Icon(icon, contentDescription = label, tint = Color.Black, modifier = Modifier.size(32.dp))
        Text(label, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun PedidosRecientesCliente(
    pedidos: List<PedidoCliente>,
    onPedidoClick: (PedidoCliente) -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Pedidos Pendientes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        if (pedidos.isEmpty()) {
            Text("Aún no has realizado pedidos.", color = TextSecondary, modifier = Modifier.padding(16.dp))
        } else {
            pedidos.take(3).forEach { pedido ->
                PedidoCard(pedido, onPedidoClick)
            }
        }
    }
}

@Composable
fun PedidoCard(pedido: PedidoCliente, onClick: (PedidoCliente) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick(pedido) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Pedido #${pedido.id}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Fecha: ${pedido.fecha}", fontSize = 13.sp, color = TextSecondary)
                Text("Total: ${pedido.total}", fontSize = 13.sp)
            }
            Text(
                pedido.estado,
                color = when (pedido.estado) {
                    "Entregado" -> GreenP
                    "Listo" -> GreenDark
                    else -> Orange
                },
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun ClienteBottomNavBar(selected: Int, onSelect: (Int) -> Unit) {
    val items = listOf("Inicio", "Catálogo", "Historial", "Cuenta")
    val icons = listOf(Icons.Default.Home, Icons.Default.List, Icons.Default.History, Icons.Default.Person)

    BottomNavigation(backgroundColor = GreenP) {
        items.forEachIndexed { idx, label ->
            BottomNavigationItem(
                icon = { Icon(icons[idx], contentDescription = label) },
                label = { Text(label) },
                selected = selected == idx,
                onClick = { onSelect(idx) },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color(0xFF666666)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HOME_ClientePreview() {
    val navController = rememberNavController()
    HOME_Cliente(
        navController = navController,
        pedidosRecientes = listOf(
            PedidoCliente("1", "2024-06-01", "Pendiente", "S/ 50.00"),
            PedidoCliente("2", "2024-06-02", "Listo", "S/ 30.00"),
            PedidoCliente("3", "2024-06-03", "Entregado", "S/ 20.00")
        )
    )
}