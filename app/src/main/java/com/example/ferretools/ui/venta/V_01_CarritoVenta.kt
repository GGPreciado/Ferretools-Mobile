package com.example.ferretools.ui.venta

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.model.database.ItemUnitario
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.AdminBottomNavBar
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.seleccion_productos.CartaProducto
import com.example.ferretools.ui.components.seleccion_productos.ScanButton
import com.example.ferretools.ui.components.seleccion_productos.SearchBar
import com.example.ferretools.ui.components.seleccion_productos.SelectorCategoria
import com.example.ferretools.viewmodel.venta.VentaViewModel
import com.example.ferretools.viewmodel.venta.VentaUiState
import com.example.ferretools.viewmodel.inventario.ListaProductosViewModel
import kotlinx.coroutines.delay

@Composable
fun V_01_CarritoVenta(
    navController: NavController,
    viewModel: VentaViewModel,
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
                .background(Color(0xFFFFE082))
                .padding(vertical = 8.dp),
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

    // Mostrar mensajes de error del ViewModel
    LaunchedEffect(uiState.status) {
        when (uiState.status) {
            VentaUiState.Status.Error -> {
                uiState.mensaje?.let { mensaje ->
                    bannerMessage = mensaje
                    showBanner = true
                }
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopNavBar(navController, "Selección de producto vendido")
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
                        .padding(
                            top = 14.dp,
                            start = 10.dp
                        )
                        .weight(0.20f),
                    onClick = { 
                        navController.navigate(AppRoutes.Sale.BARCODE_SCANNER)
                    }
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
                    Log.d("V_01_CarritoVenta", "Categoría seleccionada: $categoria, id: $catId")
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
                    val existente = uiState.productosSeleccionados.find { it.producto_id == producto.producto_id }
                    val cantidadEnCarrito = existente?.cantidad ?: 0
                    val agotado = cantidadEnCarrito >= producto.cantidad_disponible && producto.cantidad_disponible > 0
                    // Cada carta representa un producto. Si está agotado, se ve apagada y no responde al click.
                    CartaProducto(
                        producto = producto,
                        onClick = {
                            if (agotado) {
                                bannerMessage = "No hay suficiente stock para agregar más de este producto."
                                showBanner = true
                                Log.d("V_01_CarritoVenta", "Intento de agregar más productos que el stock disponible: ${producto.nombre}")
                            } else {
                                Log.d("V_01_CarritoVenta", "Producto agregado al carrito de venta: ${producto.nombre}")
                                viewModel.agregarProducto(producto, 1)
                            }
                        },
                        modifier = if (agotado) Modifier.alpha(0.4f) else Modifier
                    )
                }
            }
            // Botón para continuar al resumen del carrito de venta
            Button(
                onClick = { navController.navigate(AppRoutes.Sale.CART_SUMMARY) },
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
fun V_01_CarritoVentaPreview() {
    val navController = rememberNavController()
    V_01_CarritoVenta(
        navController = navController
    )
}

 */