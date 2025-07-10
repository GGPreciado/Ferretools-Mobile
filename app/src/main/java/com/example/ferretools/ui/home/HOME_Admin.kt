package com.example.ferretools.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.ferretools.ui.components.*
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.utils.NotificationHelper
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ferretools.viewmodel.HomeAdminViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun HOME_Admin(
    navController: NavController,
    viewModel: HomeAdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val stockAlerts by viewModel.stockAlerts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    /*
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val notificationHelper = NotificationHelper(context)
        db.collection("solicitudes")
            .whereEqualTo("estado", "pendiente")
            .addSnapshotListener { snapshot, _ ->
                val count = snapshot?.size() ?: 0
                if (count > 0) {
                    notificationHelper.mostrarNotificacionSolicitud(
                        "Nueva Solicitud de Empleo",
                        "Tienes $count solicitud(es) pendiente(s) de revisión"
                    )
                }
            }
    }*/
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
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
                    .background(Color.Gray)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.align(Alignment.Center))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Nombre de Usuario", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Nombre de la Tienda", color = Color.White, fontSize = 13.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Resumen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(title = "Ventas de esta semana", value = "0", modifier = Modifier.weight(1f))
            SummaryCard(title = "Ingresos de esta semana", value = "0 PEN", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(18.dp))
        // Accesos Directos
        Text(
            text = "Accesos Directos",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /*
            ShortcutButton("Venta", Icons.Default.ShoppingCart, Color(0xFF22D366)) {
                navController.navigate(AppRoutes.Sale.CART)
            }
            ShortcutButton("Gasto", Icons.Default.Person, Color(0xFF22D366)) {
                navController.navigate(AppRoutes.Purchase.CART)
            }
            ShortcutButton("Inventario", Icons.Default.List, Color(0xFF22D366)) {
                navController.navigate(AppRoutes.Inventory.LIST_PRODUCTS)
            }
*/
            AdminQuickAccess(
                onVenta = { navController.navigate(AppRoutes.Sale.CART) },
                onGasto = { navController.navigate(AppRoutes.Purchase.CART) },
                onInventario = { navController.navigate(AppRoutes.Inventory.LIST_PRODUCTS) }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Alertas de Stock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Alertas de Stock", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Button(
                onClick = { navController.navigate(AppRoutes.Inventory.INVENTORY_REPORT) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE082)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Text("Reporte", color = Color.Black, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                stockAlerts.isEmpty() -> {
                    Text("No hay productos con bajo stock.", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                else -> {
                    StockAlerts(
                        alerts = stockAlerts
                    )
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {}
        // Barra de navegación inferior
        AdminBottomNavBar(navController)
    }
}

@Composable
fun AdminQuickAccess(
    onVenta: () -> Unit,
    onGasto: () -> Unit,
    onInventario: () -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        /*Text(
            "Accesos Rápidos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )*/
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickAccessButtonE("Venta", Icons.Default.ShoppingCart, onVenta)
            QuickAccessButtonE("Gasto", Icons.Default.Person, onGasto)
            QuickAccessButtonE("Tienda", Icons.Default.List, onInventario)
        }
    }
}

@Composable
fun QuickAccessButtonA(label: String, icon: ImageVector, onClick: () -> Unit) {
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

@Preview(showBackground = true)
@Composable
fun HOME_AdminPreview() {
    val navController = rememberNavController()
    HOME_Admin(navController = navController)
}