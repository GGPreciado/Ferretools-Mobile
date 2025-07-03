package com.example.ferretools.ui.venta

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
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
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.viewmodel.venta.VentaViewModel

@Composable
fun V_04_VentaExitosa(
    navController: NavController,
    viewModel: VentaViewModel = viewModel()
) {
    Scaffold(
        topBar = { TopNavBar(navController, "Venta Exitosa") }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono de éxito
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Venta exitosa",
                modifier = Modifier.size(120.dp),
                tint = Color(0xFF4CAF50)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Mensaje de éxito
            Text(
                text = "¡Venta registrada exitosamente!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "La venta ha sido registrada y el inventario ha sido actualizado.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Botones de acción
            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón para ver boleta
                Button(
                    onClick = { navController.navigate(AppRoutes.Sale.RECEIPT) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ver Boleta", color = Color.Black, fontSize = 16.sp)
                }

                // Botón para nueva venta
                Button(
                    onClick = { 
                        viewModel.resetState()
                        navController.navigate(AppRoutes.Sale.CART) {
                            popUpTo(AppRoutes.Sale.CART) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Nueva Venta", color = Color.White, fontSize = 16.sp)
                }

                // Botón para volver al inicio
                Button(
                    onClick = { 
                        viewModel.resetState()
                        navController.navigate(AppRoutes.Admin.DASHBOARD) {
                            popUpTo(AppRoutes.Admin.DASHBOARD) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Volver al Inicio", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun V_04_VentaExitosaPreview() {
    val navController = rememberNavController()
    V_04_VentaExitosa(
        navController = navController
    )
}