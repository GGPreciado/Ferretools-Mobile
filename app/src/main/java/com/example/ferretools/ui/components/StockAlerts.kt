package com.example.ferretools.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

// Colores para las alertas
val RedAlert = Color(0xFFFF8A80)
val YellowAlert = Color(0xFFFFF59D)
val Black = Color(0xFF333333)

// Modelo de datos para la alerta de stock
data class StockAlert(val product: String, val units: Int, val isLow: Boolean)

@Composable
fun StockAlerts(alerts: List<StockAlert>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(alerts) { alert ->
            StockAlertCard(alert)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun StockAlertCard(alert: StockAlert) {
    val bgColor = if (alert.isLow) RedAlert else YellowAlert
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(alert.product, fontWeight = FontWeight.Bold, color = Black, fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
        }
        Text("Quedan ${alert.units} unidades.", color = Black)
    }
} 