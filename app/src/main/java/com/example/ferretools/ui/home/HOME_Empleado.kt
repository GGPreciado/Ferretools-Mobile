package com.example.ferretools.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ferretools.viewmodel.HomeEmpleadoViewModel

data class PedidoPendiente(
    val id: String,
    val cliente: String,
    val fecha: String,
    val productos: List<String>,
    val estado: String
)

@Composable
fun HOME_Empleado(
    navController: NavController,
    pedidosPendientes: List<PedidoPendiente> = emptyList(),
    viewModel: HomeEmpleadoViewModel = viewModel()
) {
    // Observar datos del usuario y negocio
    val userName = viewModel.userName.collectAsState().value
    val storeName = viewModel.storeName.collectAsState().value
    
    Scaffold(
        topBar = { TopNavBarEmpleado(userName, storeName) },
        bottomBar = {
            EmpleadoBottomNavBar(
                onInicio = { /* Pantalla actual */ },
                onInventario = { navController.navigate(AppRoutes.Employee.INVENTORY) },
                onHistorial = { navController.navigate(AppRoutes.Order.Employee.HISTORY) },
                onCuenta = { navController.navigate(AppRoutes.Config.MAIN) }
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(Modifier.height(24.dp))
            AlmaceneroQuickAccess(
                onCompra = { navController.navigate(AppRoutes.Sale.CART) },
                onVenta = { navController.navigate(AppRoutes.Purchase.CART) },
                onHistorial = { navController.navigate(AppRoutes.Order.Employee.HISTORY) },
                onCatalogo = { navController.navigate(AppRoutes.Employee.INVENTORY) }
            )
            Spacer(Modifier.height(24.dp))
            ListaPedidosPendientes(
                pedidos = pedidosPendientes,
                onPedidoClick = { pedido ->
                    navController.navigate(AppRoutes.Order.Employee.DETAILS)
                }
            )
        }
    }
}

@Composable
fun TopNavBarEmpleado(userName: String, storeName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF22D366))
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
fun EmpleadoBottomNavBar(
    onInicio: () -> Unit,
    onHistorial: () -> Unit,
    onInventario: () -> Unit,
    onCuenta: () -> Unit
) {
    NavigationBar(containerColor = Color(0xFF22D366)) {
        NavigationBarItem(
            selected = false,
            onClick = onInicio,
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onInventario,
            icon = { Icon(Icons.Default.List, contentDescription = "Inventario") },
            label = { Text("Inventario") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onHistorial,
            icon = { Icon(Icons.Default.History, contentDescription = "Historial") },
            label = { Text("Historial") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onCuenta,
            icon = { Icon(Icons.Default.Person, contentDescription = "Cuenta") },
            label = { Text("Cuenta") }
        )
    }
}

@Composable
fun AlmaceneroQuickAccess(
    onCompra: () -> Unit,
    onVenta: () -> Unit,
    onCatalogo: () -> Unit,
    onHistorial: () -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Accesos Rápidos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickAccessButtonE("Venta", Icons.Default.ShoppingCart, onCompra)
            QuickAccessButtonE("Compra", Icons.Default.ShoppingCart, onVenta)
            QuickAccessButtonE("Tienda", Icons.Default.List, onCatalogo)
            QuickAccessButtonE("Historial", Icons.Default.History, onHistorial)
        }
    }
}

@Composable
fun QuickAccessButtonE(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF22D366), shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Icon(icon, contentDescription = label, tint = Color.Black, modifier = Modifier.size(32.dp))
        Text(label, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun ListaPedidosPendientes(pedidos: List<PedidoPendiente>, onPedidoClick: (PedidoPendiente) -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Pedidos Pendientes",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(vertical = 12.dp)
        )
        if (pedidos.isEmpty()) {
            Text("No hay pedidos pendientes.", color = Color.Gray, modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn {
                items(pedidos) { pedido ->
                    PedidoPendienteCard(pedido = pedido, onClick = { onPedidoClick(pedido) })
                }
            }
        }
    }
}

@Composable
fun PedidoPendienteCard(pedido: PedidoPendiente, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Pedido #${pedido.id}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.weight(1f))
                Text(pedido.estado, color = Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text("Cliente: ${pedido.cliente}", fontSize = 14.sp)
            Text("Fecha: ${pedido.fecha}", fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text("Productos:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            pedido.productos.forEach {
                Text("- $it", fontSize = 13.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HOME_EmpleadoPreview() {
    val navController = rememberNavController()
    HOME_Empleado(
        navController = navController,
        pedidosPendientes = listOf(
            PedidoPendiente("1", "Cliente 1", "2024-06-01", listOf("Producto A", "Producto B"), "Pendiente"),
            PedidoPendiente("2", "Cliente 2", "2024-06-02", listOf("Producto C"), "Pendiente")
        )
    )
}