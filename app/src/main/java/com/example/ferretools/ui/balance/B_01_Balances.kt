package com.example.ferretools.ui.balance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.R
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.AdminBottomNavBar
import com.example.ferretools.ui.components.SelectorOpciones
import com.example.ferretools.ui.components.UserDataBar
import com.example.ferretools.ui.components.detalles_cv.CampoFechaSeleccion
import com.example.ferretools.viewmodel.HomeAdminViewModel

private val YellowPrimary = Color(0xFFFFEB3B)
private val GreenLight = Color(0xFFB9F6CA)
private val RedLight = Color(0xFFFF8A80)
private val GreenText = Color(0xFF22D366)
private val RedText = Color.Red
private val CardBorder = Color.Black

data class BalanceResumen(
    val total: Double,
    val ingresos: Double,
    val egresos: Double
)

data class Movimiento(
    val productos: String,
    val fecha: String,
    val monto: Double,
    val metodo: String
)

@Composable
fun B_01_Balances(
    navController: NavController,
    homeAdminViewModel: HomeAdminViewModel = viewModel()
    // viewModel: BalanceViewModel = viewModel() // Para uso futuro
) {
    // Observar datos del usuario y negocio
    val userName = homeAdminViewModel.userName.collectAsState().value
    val storeName = homeAdminViewModel.storeName.collectAsState().value
    
    // Ejemplo de datos mockeados
    val resumen = BalanceResumen(1200.0, 2000.0, 800.0)
    val movimientos = listOf(
        Movimiento("Arroz x2, Azúcar x1", "2024-06-10", 150.0, "Efectivo"),
        Movimiento("Leche x3, Pan x5", "2024-06-11", 80.0, "Tarjeta")
    )
    var filtro by remember { mutableStateOf("Ingresos") }

    Scaffold(
        topBar = { UserDataBar(userName, storeName) },
        bottomBar = { AdminBottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Fecha", style = MaterialTheme.typography.titleMedium)
            CampoFechaSeleccion()

            Text("Balance", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(2.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", style = MaterialTheme.typography.bodyLarge)
                        Text("S/ ${resumen.total}", style = MaterialTheme.typography.bodyLarge)
                    }
                    Divider(Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("Ingresos", color = GreenText, style = MaterialTheme.typography.bodyMedium)
                            Text("S/ ${resumen.ingresos}", color = GreenText, style = MaterialTheme.typography.bodyLarge)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("Egresos", color = RedText, style = MaterialTheme.typography.bodyMedium)
                            Text("S/ ${resumen.egresos}", color = RedText, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Divider(Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { navController.navigate(AppRoutes.Balance.DETAILS) },
                            colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
                        ) {
                            Text("Ver detalles", color = Color.Black)
                        }
                        Button(
                            onClick = { /* TODO: Exportar a PDF */ },
                            colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
                        ) {
                            Text("Convertir a PDF", color = Color.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SelectorOpciones(
                opcion1 = "Ingresos",
                opcion2 = "Egresos",
                seleccionado = filtro
            ) { filtro = it }

            Spacer(modifier = Modifier.height(12.dp))

            ListaMovimientos(movimientos = movimientos.filter {
                if (filtro == "Ingresos") it.monto >= 0 else it.monto < 0
            })

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate(AppRoutes.Balance.REPORT) },
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 15.dp)
            ) {
                Text("Reporte", color = Color.Black)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate(AppRoutes.Sale.CART) },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenLight)
                ) {
                    Text("Agregar venta", color = Color.Black)
                }
                Button(
                    onClick = { navController.navigate(AppRoutes.Purchase.CART) },
                    colors = ButtonDefaults.buttonColors(containerColor = RedLight)
                ) {
                    Text("Agregar compra", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ListaMovimientos(movimientos: List<Movimiento>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        if (movimientos.isEmpty()) {
            Text("No hay movimientos.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        } else {
            movimientos.forEach { mov ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painterResource(R.drawable.inventario),
                            contentDescription = "Producto",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(mov.productos, style = MaterialTheme.typography.bodyMedium)
                        Text(mov.fecha, style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("S/ ${mov.monto}", style = MaterialTheme.typography.bodyLarge)
                        Text(mov.metodo, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewB_01_Balances() {
    val navController = rememberNavController()
    B_01_Balances(navController = navController)
}