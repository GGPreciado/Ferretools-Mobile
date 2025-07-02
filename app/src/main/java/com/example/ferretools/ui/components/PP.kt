package com.example.ferretools.ui.components
/*
// === Imports ===
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

// === Colores globales ===
val GreenPrimary = Color(0xFF00E600)
val GrayLight = Color(0xFFF0F0F0)
val GrayMedium = Color(0xFF666666)
val RedAlert = Color(0xFFFF8A80)
val YellowAlert = Color(0xFFFFF59D)
val White = Color.White
val Black = Color(0xFF333333)

// === Modelos de datos ===
data class StockAlert(val product: String, val units: Int, val isLow: Boolean)

// === Composables ===

@Composable
fun UserHeader(userName: String, storeName: String, avatarUrl: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        avatarUrl?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = null,
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(8.dp))
        }
        Column {
            Text(userName, fontSize = 16.sp, color = Black)
            Text(storeName, fontSize = 16.sp, color = Black)
        }
    }
}

@Composable
fun FinancialSummary(sales: String, income: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SummaryCard("Ventas de esta semana", sales)
        SummaryCard("Ingresos de esta semana", income)
    }
}

@Composable
fun SummaryCard(label: String, value: String) {
    Column(
        modifier = Modifier
            .background(White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
            .width(140.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 14.sp, color = Black)
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Black)
    }
}

@Composable
fun QuickAccess(onVenta: () -> Unit, onGasto: () -> Unit, onInventario: () -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text("Accesos Directos", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Black)
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickAccessButton("Venta", Icons.Default.ShoppingCart, onVenta)
            QuickAccessButton("Gasto", Icons.Default.Receipt, onGasto)
            QuickAccessButton("Inventario", Icons.Default.Inventory, onInventario)
        }
    }
}

@Composable
fun QuickAccessButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(GreenPrimary, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Icon(icon, contentDescription = label, tint = Black, modifier = Modifier.size(32.dp))
        Text(label, fontWeight = FontWeight.Bold, color = Black)
    }
}

@Composable
fun StockAlerts(alerts: List<StockAlert>, onReport: () -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alertas de Stock", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Black)
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onReport,
                colors = ButtonDefaults.buttonColors(containerColor = YellowAlert)
            ) {
                Text("Reporte", fontWeight = FontWeight.Bold, color = Black)
            }
        }
        Spacer(Modifier.height(8.dp))
        alerts.forEach {
            StockAlertCard(it)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun StockAlertCard(alert: StockAlert) {
    val bgColor = if (alert.isLow) RedAlert else YellowAlert
    //val arrow = if (alert.isLow) "↓" else "↑"
    //val arrowColor = if (alert.isLow) Color.Red else Color.Green

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(alert.product, fontWeight = FontWeight.Bold, color = Black, fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            //Text(arrow, color = arrowColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Text("Quedan ${alert.units} unidades.", color = Black)
    }
}

@Composable
fun BottomMenu(selected: Int, onSelect: (Int) -> Unit) {
    val items = listOf("Inicio", "Balance", "Inventario", "Cuenta")
    val icons = listOf(Icons.Default.Home, Icons.Default.ShowChart, Icons.Default.Inventory, Icons.Default.Person)

    BottomNavigation(backgroundColor = GreenPrimary) {
        items.forEachIndexed { idx, label ->
            BottomNavigationItem(
                icon = { Icon(icons[idx], contentDescription = label) },
                label = { Text(label) },
                selected = selected == idx,
                onClick = { onSelect(idx) },
                selectedContentColor = Black,
                unselectedContentColor = GrayMedium
            )
        }
    }
}

@Composable
fun DashboardScreen(
    userName: String,
    storeName: String,
    avatarUrl: String?,
    sales: String,
    income: String,
    alerts: List<StockAlert>,
    selectedMenu: Int,
    onMenuSelect: (Int) -> Unit,
    onVenta: () -> Unit,
    onGasto: () -> Unit,
    onInventario: () -> Unit,
    onReport: () -> Unit
) {
    Scaffold(bottomBar = { BottomMenu(selected = selectedMenu, onSelect = onMenuSelect) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(padding)
        ) {
            UserHeader(userName, storeName, avatarUrl)
            Spacer(Modifier.height(8.dp))
            FinancialSummary(sales, income)
            Divider(color = GrayLight, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
            QuickAccess(onVenta, onGasto, onInventario)
            Divider(color = GrayLight, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
            StockAlerts(alerts, onReport)
        }
    }
}

// === Preview ===

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(
        userName = "Nombre de Usuario",
        storeName = "Nombre de la Tienda",
        avatarUrl = null,
        sales = "0",
        income = "0 PEN",
        alerts = listOf(
            StockAlert("Producto A", 2, true),
            StockAlert("Producto B", 5, false),
            StockAlert("Producto C", 1, true)
        ),
        selectedMenu = 0,
        onMenuSelect = {},
        onVenta = {},
        onGasto = {},
        onInventario = {},
        onReport = {}
    )
}


 */