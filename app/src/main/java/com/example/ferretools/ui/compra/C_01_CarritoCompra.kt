package com.example.ferretools.ui.compra

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.AdminBottomNavBar
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.seleccion_productos.CartaProducto
import com.example.ferretools.ui.components.seleccion_productos.ScanButton
import com.example.ferretools.ui.components.seleccion_productos.SearchBar
import com.example.ferretools.ui.components.seleccion_productos.SelectorCategoria
import com.example.ferretools.viewmodel.compra.CompraViewModel
import com.example.ferretools.viewmodel.inventario.ListaProductosViewModel
import com.example.ferretools.model.database.Producto
import com.example.ferretools.model.database.ItemUnitario
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun C_01_CarritoCompra(
    navController: NavController,
    viewModel: CompraViewModel,
    listaProductosViewModel: ListaProductosViewModel = viewModel()
) {
    val productosUiState = listaProductosViewModel.uiState.collectAsState().value
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var bannerMessage by remember { mutableStateOf("") }
    var showBanner by remember { mutableStateOf(false) }

    // Filtrado por búsqueda
    val productosFiltrados = productosUiState.productosFiltrados.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) ||
        it.codigo_barras.contains(searchQuery, ignoreCase = true)
    }

    // Banner superior para advertencias
    if (showBanner) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFE082), RoundedCornerShape(0.dp))
                .padding(vertical = 8.dp)
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                bannerMessage,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
        // Ocultar banner después de 2 segundos
        LaunchedEffect(bannerMessage) {
            delay(2000)
            showBanner = false
        }
    }

    Scaffold(
        topBar = {
            TopNavBar(navController, "Selección de producto comprado")
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
                // SearchBar permite buscar productos por nombre o código
                SearchBar(
                    Modifier.weight(0.80f),
                    value = searchQuery,
                    onValueChange = { searchQuery = it }
                )
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

            // SelectorCategoria permite filtrar productos por categoría
            SelectorCategoria(
                categorias = productosUiState.categoriasName,
                onCategoriaSeleccionada = { categoria ->
                    categoriaSeleccionada = categoria
                    // Obtener el id real de la categoría seleccionada
                    val catId = productosUiState.categorias.getOrNull(productosUiState.categoriasName.indexOf(categoria) - 1)?.id ?: ""
                    Log.d("C_01_CarritoCompra", "Categoría seleccionada: $categoria, id: $catId")
                    listaProductosViewModel.filtrarPorCategoria(if (categoria == "Todas las categorías") "" else catId)
                },
                categoriaSeleccionada = categoriaSeleccionada
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Cuadrícula de productos mostrados según búsqueda y filtro
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f)
            ) {
                items(productosFiltrados.size) { idx ->
                    val producto = productosFiltrados[idx]
                    // Eliminar validación de stock para compras
                    CartaProducto(
                        producto = producto,
                        onClick = {
                            viewModel.agregarProducto(ItemUnitario(
                                cantidad = 1,
                                subtotal = producto.precio,
                                producto_id = producto.producto_id
                            ))
                            Log.d("C_01_CarritoCompra", "Producto agregado al carrito: ${producto.nombre}")
                        },
                        modifier = Modifier // Siempre habilitado
                    )
                }
            }
            // Botón para continuar al resumen del carrito
            Button(
                onClick = { navController.navigate(AppRoutes.Purchase.CART_SUMMARY) },
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
/*
@Preview(showBackground = true)
@Composable
fun C_01_CarritoCompraPreview() {
    val navController = rememberNavController()
    C_01_CarritoCompra(navController = navController)
}

 */