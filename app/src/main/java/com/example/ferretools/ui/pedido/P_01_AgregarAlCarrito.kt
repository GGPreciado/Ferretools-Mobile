package com.example.ferretools.ui.pedido

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.ferretools.viewmodel.pedido.PedidoViewModel
import com.example.ferretools.viewmodel.inventario.ListaProductosViewModel

@Composable
fun P_01_AgregarAlCarrito(
    navController: NavController,
    viewModel: PedidoViewModel,
    listaProductosViewModel: ListaProductosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val productosUiState = listaProductosViewModel.uiState.collectAsState().value
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var bannerMessage by remember { mutableStateOf("") }
    var showBanner by remember { mutableStateOf(false) }

    val productosFiltrados = productosUiState.productosFiltrados.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) ||
        it.codigo_barras.contains(searchQuery, ignoreCase = true)
    }

    if (showBanner) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFE082))
                .padding(vertical = 8.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                bannerMessage,
                color = Color.Black
            )
        }
        LaunchedEffect(bannerMessage) {
            kotlinx.coroutines.delay(2000)
            showBanner = false
        }
    }

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
                SearchBar(
                    Modifier.weight(0.80f),
                    value = searchQuery,
                    onValueChange = { searchQuery = it }
                )
                ScanButton(
                    Modifier
                        .padding(top = 14.dp, start = 10.dp)
                        .weight(0.20f)
                        .size(45.dp)
                        .clickable { /* TODO: Pantalla de Escanear producto */ },
                    onClick = {
                        navController.navigate(AppRoutes.Sale.BARCODE_SCANNER)
                    }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            SelectorCategoria(
                categorias = productosUiState.categoriasName,
                onCategoriaSeleccionada = { categoria ->
                    categoriaSeleccionada = categoria
                    val catId = productosUiState.categorias.getOrNull(productosUiState.categoriasName.indexOf(categoria) - 1)?.id ?: ""
                    listaProductosViewModel.filtrarPorCategoria(if (categoria == "Todas las categorías") "" else catId)
                },
                categoriaSeleccionada = categoriaSeleccionada
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f)
            ) {
                items(productosFiltrados.size) { idx ->
                    val producto = productosFiltrados[idx]
                    val existente = uiState.productosSeleccionados.find { it.producto_id == producto.producto_id }
                    val cantidadEnCarrito = existente?.cantidad ?: 0
                    val agotado = cantidadEnCarrito >= producto.cantidad_disponible && producto.cantidad_disponible > 0
                    CartaProducto(
                        producto = producto,
                        onClick = {
                            if (agotado) {
                                bannerMessage = "No hay suficiente stock para agregar más de este producto."
                                showBanner = true
                            } else {
                                viewModel.agregarProducto(producto, 1)
                            }
                        },
                        modifier = if (agotado) Modifier.alpha(0.4f) else Modifier
                    )
                }
            }
            // Botón Continuar
            Button(
                onClick = { navController.navigate(AppRoutes.Order.CART) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                enabled = uiState.productosSeleccionados.isNotEmpty()
            ) {
                Text("Continuar", color = Color.Black)
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun P_01_AgregarAlCarritoPreview() {
    val navController = rememberNavController()
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<PedidoViewModel>()
    val listaProductosViewModel = androidx.lifecycle.viewmodel.compose.viewModel<ListaProductosViewModel>()
    P_01_AgregarAlCarrito(navController = navController, viewModel = viewModel, listaProductosViewModel = listaProductosViewModel)
}

 */