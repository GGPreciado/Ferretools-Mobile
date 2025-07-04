package com.example.ferretools.ui.pedido

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.boleta.BoletaNavBar
import com.example.ferretools.ui.components.boleta.DetalleProductoFila
import com.example.ferretools.viewmodel.pedido.PedidoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun P_06_BoletaPedido(
    navController: NavController,
    viewModel: PedidoViewModel
) {
    val pedido = viewModel.ultimoPedidoExitoso
    Scaffold(
        topBar = { TopNavBar(navController, "Boleta de pedido") }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(400.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Fecha de pedido", style = MaterialTheme.typography.bodyMedium)
                            Text("Medio de pago", style = MaterialTheme.typography.bodyMedium)
                        }
                        Column {
                            val fechaFormateada = pedido?.fecha?.toDate()?.let {
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                            } ?: "N/A"
                            val metodoPago = when (pedido?.metodoPago) {
                                com.example.ferretools.model.enums.MetodosPago.Efectivo -> "Efectivo"
                                com.example.ferretools.model.enums.MetodosPago.Yape -> "Yape"
                                else -> "N/A"
                            }
                            Text(fechaFormateada, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(metodoPago, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Productos:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Nombre", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("Cantidad", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("Precio", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Divider()
                    pedido?.productosConDetalles?.forEach { (item, producto) ->
                        DetalleProductoFila(
                            nombre = producto?.nombre ?: "Producto desconocido",
                            cantidad = (item.cantidad ?: 0).toString(),
                            precio = "S/ ${String.format("%.2f", item.subtotal ?: 0.0)}"
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("S/ ${String.format("%.2f", pedido?.total ?: 0.0)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            BoletaNavBar(navController)
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun PreviewP_06_BoletaPedido() {
    val navController = rememberNavController()
    val viewModel = PedidoViewModel() // Assuming PedidoViewModel is available for preview
    P_06_BoletaPedido(navController = navController, viewModel = viewModel)
}

 */