package com.example.ferretools.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.AdminBottomNavBar
import com.example.ferretools.ui.components.StockAlerts
import com.example.ferretools.ui.components.SummaryCard
import com.example.ferretools.ui.components.UserDataBar
import com.example.ferretools.viewmodel.HomeAdminViewModel

@Composable
fun HOME_Admin(
    navController: NavController,
    viewModel: HomeAdminViewModel = viewModel()
) {
    val stockAlerts by viewModel.stockAlerts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Observar datos del usuario y negocio
    val userName = viewModel.userName.collectAsState().value
    val storeName = viewModel.storeName.collectAsState().value

    Scaffold(
        topBar = { UserDataBar(userName, storeName) },
        bottomBar = { AdminBottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // Resumen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Ventas de esta semana",
                    value = "0",
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Ingresos de esta semana",
                    value = "0 PEN",
                    modifier = Modifier.weight(1f)
                )
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
                        Text(
                            "No hay productos con bajo stock.",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    else -> {
                        StockAlerts(
                            alerts = stockAlerts
                        )
                    }
                }
            }
        }
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

//@Preview(showBackground = true)
//@Composable
//fun HOME_AdminPreview() {
//    val navController = rememberNavController()
//    HOME_Admin(navController = navController)
//}