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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.AdminBottomNavBar
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.seleccion_productos.CartaProducto
import com.example.ferretools.ui.components.seleccion_productos.ScanButton
import com.example.ferretools.ui.components.seleccion_productos.SearchBar
import com.example.ferretools.ui.components.seleccion_productos.SelectorCategoria
import com.example.ferretools.viewmodel.pedido.PedidoViewModel
import com.example.ferretools.viewmodel.pedido.PedidoUiState
import com.example.ferretools.viewmodel.inventario.ListaProductosViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

@Composable
fun P_01_AgregarAlCarrito(
    navController: NavController,
    viewModel: PedidoViewModel,
    listaProductosViewModel: ListaProductosViewModel = viewModel()
) {
    val productosUiState = listaProductosViewModel.uiState.collectAsState().value
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var bannerMessage by remember { mutableStateOf("") }
    var showBanner by remember { mutableStateOf(false) }

    // --- INTEGRACIÓN MEJORADA DEL ESCÁNER DE CÓDIGO DE BARRAS EN PEDIDOS ---
    LaunchedEffect(Unit) {
        while (true) {
            delay(100) // Polling cada 100ms para detectar cambios
            val backStackEntry = navController.currentBackStackEntry
            val scannedBarcode = backStackEntry?.savedStateHandle?.get<String>("barcode_result")
            
            if (scannedBarcode != null) {
                Log.d("P_01_AgregarAlCarrito", "Código de barras escaneado detectado: $scannedBarcode")
                
                // Limpiar el código escaneado para evitar procesamiento duplicado
                backStackEntry.savedStateHandle.remove<String>("barcode_result")
                
                // Usar el ViewModel para buscar y agregar el producto
                viewModel.buscarProductoPorCodigoBarras(scannedBarcode)
                
                Log.d("P_01_AgregarAlCarrito", "Procesando código de barras: $scannedBarcode")
            }
        }
    }

    // Mostrar mensajes del ViewModel solo para errores
    LaunchedEffect(uiState.status) {
        when (uiState.status) {
            PedidoUiState.Status.Error -> {
                uiState.mensaje?.let { mensaje ->
                    bannerMessage = mensaje
                    showBanner = true
                    Log.d("P_01_AgregarAlCarrito", "Error del ViewModel: $mensaje")
                }
                viewModel.resetState()
            }
            // No mostrar mensajes de éxito para mantener consistencia con el flujo de ventas
            else -> {}
        }
    }

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
                        .padding(
                            top = 14.dp,
                            start = 10.dp
                        )
                        .weight(0.20f),
                    onClick = {
                        Log.d("P_01_AgregarAlCarrito", "Navegando al escáner de código de barras (pedidos)")
                        navController.navigate(AppRoutes.Order.BARCODE_SCANNER)
                    }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            SelectorCategoria(
                categorias = productosUiState.categoriasName,
                onCategoriaSeleccionada = { categoria ->
                    categoriaSeleccionada = categoria
                    val catId = productosUiState.categorias.getOrNull(productosUiState.categoriasName.indexOf(categoria) - 1)?.id ?: ""
                    Log.d("P_01_AgregarAlCarrito", "Categoría seleccionada: $categoria, id: $catId")
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
                    val agotado = producto.cantidad_disponible <= 0 || cantidadEnCarrito >= producto.cantidad_disponible
                    CartaProducto(
                        producto = producto,
                        onClick = {
                            if (agotado) {
                                if (producto.cantidad_disponible <= 0) {
                                    bannerMessage = "Producto sin stock disponible."
                                } else {
                                    bannerMessage = "No hay suficiente stock para agregar más de este producto."
                                }
                                showBanner = true
                                Log.d("P_01_AgregarAlCarrito", "Intento de agregar producto sin stock: ${producto.nombre}")
                            } else {
                                viewModel.agregarProducto(producto, 1)
                                Log.d("P_01_AgregarAlCarrito", "Producto agregado al carrito: ${producto.nombre}")
                            }
                        },
                        modifier = if (agotado) Modifier.alpha(0.4f) else Modifier
                    )
                }
            }
            // Botón Continuar
            Button(
                onClick = { 
                    Log.d("P_01_AgregarAlCarrito", "Navegando al carrito de pedidos")
                    navController.navigate(AppRoutes.Order.CART) 
                },
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