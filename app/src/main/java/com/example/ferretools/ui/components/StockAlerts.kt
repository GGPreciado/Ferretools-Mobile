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

// Colores para las alertas
val RedAlert = Color(0xFFFF8A80)
val YellowAlert = Color(0xFFFFF59D)
val Black = Color(0xFF333333)

// Modelo de datos para la alerta de stock
data class StockAlert(val product: String, val units: Int, val isLow: Boolean)

@Composable
fun StockAlerts(alerts: List<StockAlert>) {
    Column(Modifier.padding(horizontal = 0.dp)) {
        /*
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alertas de Stock", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Black)
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onReport,
                colors = ButtonDefaults.buttonColors(containerColor = YellowAlert)
            ) {
                Text("Reporte", fontWeight = FontWeight.Bold, color = Black)
            }
        }
        Spacer(Modifier.height(8.dp))

         */
        alerts.forEach {
            StockAlertCard(it)
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