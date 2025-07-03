package com.example.ferretools.ui.pedido

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.AdminBottomNavBar
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.seleccion_productos.CartaProducto
import com.example.ferretools.ui.components.seleccion_productos.ScanButton
import com.example.ferretools.ui.components.seleccion_productos.SearchBar
import com.example.ferretools.ui.components.seleccion_productos.SelectorCategoria

@Composable
fun P_01_AgregarAlCarrito(
    navController: NavController,
    // viewModel: PedidoCarritoViewModel = viewModel() // Para uso futuro
) {
    Scaffold(
        topBar = {
            TopNavBar(navController, "Selección de producto a pedir")
        },
        bottomBar = {
            AdminBottomNavBar(navController, Modifier.size(40.dp))
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Buscador y escáner de barras
            Row {
                //SearchBar(Modifier.weight(0.80f))=====================================================> Por COmpra

                ScanButton(
                    Modifier
                        .padding(
                            top = 14.dp,
                            start = 10.dp
                        )
                        .weight(0.20f)
                        .size(45.dp)
                        .clickable { /* TODO: Pantalla de Escanear producto */ }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Categorías
            //SelectorCategoria()=====================================================> Por COmpra

            Spacer(modifier = Modifier.height(20.dp))

            // Cuadrícula de productos
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f)
            ) {
                items(9) {
                    //CartaProducto() =====================================================> Por COmpra
                }
            }
            // Botón Continuar
            Button(
                onClick = { navController.navigate(AppRoutes.Order.CART) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
            ) {
                Text("Continuar", color = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun P_01_AgregarAlCarritoPreview() {
    val navController = rememberNavController()
    P_01_AgregarAlCarrito(navController = navController)
}