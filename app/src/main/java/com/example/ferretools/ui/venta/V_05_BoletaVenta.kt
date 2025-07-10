package com.example.ferretools.ui.venta

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.boleta.BoletaNavBar
import com.example.ferretools.ui.components.boleta.DetalleProductoFila
import com.example.ferretools.viewmodel.venta.VentaViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun V_05_BoletaVenta(
    navController: NavController,
    viewModel: VentaViewModel = viewModel()
) {
    Scaffold(
        topBar = { TopNavBar(navController, "Boleta de venta") }
    ) { padding ->
        val uiState by viewModel.uiState.collectAsState()
        val ventaExitosa = viewModel.ultimaVentaExitosa
        
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Caja principal con detalles
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(400.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        // Encabezados
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Fecha de venta", fontSize = 14.sp)
                                Text("Medio de pago", fontSize = 14.sp)
                            }
                            Column {
                                val fechaFormateada = ventaExitosa?.fecha?.toDate()?.let { 
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                                } ?: "N/A"
                                val metodoPago = when (ventaExitosa?.metodoPago) {
                                    com.example.ferretools.model.enums.MetodosPago.Efectivo -> "Efectivo"
                                    com.example.ferretools.model.enums.MetodosPago.Yape -> "Yape"
                                    else -> "N/A"
                                }
                                Text(fechaFormateada, fontSize = 14.sp)
                                Text(metodoPago, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Productos:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        // Tabla de productos
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Nombre", fontWeight = FontWeight.Bold)
                            Text("Cantidad", fontWeight = FontWeight.Bold)
                            Text("Precio", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider()
                    }
                    
                    // Productos
                    items(ventaExitosa?.productosConDetalles ?: emptyList()) { (item, producto) ->
                        DetalleProductoFila(
                            nombre = producto?.nombre ?: "Producto desconocido",
                            cantidad = (item.cantidad ?: 0).toString(),
                            precio = "$ ${String.format("%.2f", item.subtotal ?: 0.0)}"
                        )
                    }
                    
                    item {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        // Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TOTAL", fontWeight = FontWeight.Bold)
                            Text("$ ${String.format("%.2f", ventaExitosa?.total ?: 0.0)}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Acciones
            BoletaNavBar(navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun V_05_BoletaVentaPreview() {
    val navController = rememberNavController()
    V_05_BoletaVenta(
        navController = navController
    )
}