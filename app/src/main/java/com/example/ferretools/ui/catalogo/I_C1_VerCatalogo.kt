package com.example.ferretools.ui.catalogo

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.home.ClienteBottomNavBar
import com.example.ferretools.ui.components.SummaryCard
import com.example.ferretools.viewmodel.inventario.ListaProductosViewModel
import com.example.ferretools.ui.home.HomeViewModel

@Composable
fun I_C1_VerCatalogo(
    navController: NavController,
    listaProductosViewModel: ListaProductosViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    var selectedCategory by remember { mutableStateOf("Todas las categorías") }

    val uiState = listaProductosViewModel.uiState.collectAsState().value
    val scrollState = rememberScrollState()
    
    // Observar datos del usuario y negocio
    val userName = homeViewModel.userName.collectAsState().value
    val storeName = homeViewModel.storeName.collectAsState().value
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header (fijo)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF22D366))
                .padding(vertical = 10.dp, horizontal = 8.dp)
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = userName, 
                    color = MaterialTheme.colorScheme.onPrimary, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = storeName, 
                    color = MaterialTheme.colorScheme.onPrimary, 
                    fontSize = 13.sp
                )
            }
        }
        
//        // Botón de reportes (fijo)
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Button(
//                onClick = { navController.navigate(AppRoutes.Inventory.INVENTORY_REPORT) },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.secondaryContainer
//                ),
//                shape = RoundedCornerShape(6.dp),
//                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp)
//            ) {
//                Icon(
//                    Icons.AutoMirrored.Filled.List,
//                    contentDescription = "Reportes",
//                    tint = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//                Spacer(modifier = Modifier.width(6.dp))
//                Text(
//                    "Reportes",
//                    color = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(10.dp))
        
        // Resumen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SummaryCard(title = "Total de Productos", value = uiState.productos.size.toString())

            val valorTotal = uiState.productos.sumOf { it.precio * it.cantidad_disponible }
            SummaryCard(title = "Valor Total", value = "${valorTotal} PEN")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filtros de categorías
        LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
            Log.e("DEBUG", "Cat: ${uiState.categoriasName}")
            items(uiState.categoriasName) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = {
                        selectedCategory = cat
                        if (selectedCategory == "Todas las categorías") {
                            listaProductosViewModel.filtrarPorCategoria("")
                        } else {
                            // Buscar el id de la categoría seleccionada por nombre
                            val categoriaId = uiState.categorias.find { it.nombre == selectedCategory }?.id
                            listaProductosViewModel.filtrarPorCategoria(categoriaId ?: "")
                        }
                    },
                    label = { Text(cat) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Contenido desplazable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // Lista de productos desde Firestore
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
               uiState.productosFiltrados.forEach { producto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                navController.navigate(
                                    AppRoutes.Client.PRODUCT_DETAILS(
                                        productoId = producto.producto_id
                                    )
                                )
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                // Aquí podrías cargar la imagen si tienes una URL
                                Icon(
                                    Icons.Default.Person, 
                                    contentDescription = "Imagen producto", 
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(producto.nombre, fontWeight = FontWeight.Bold)
                                Text("S/ ${producto.precio}", color = Color.Gray)
                                Text("Stock: ${producto.cantidad_disponible}", color = Color.Gray)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        
        // Botón de categorías (sin el botón de agregar producto)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 32.dp, vertical = 20.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate("inventory_categories?isReadOnly=true")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Categorías", 
                    color = MaterialTheme.colorScheme.onSecondary, 
                    fontSize = 18.sp
                )
            }
        }
        
        // Barra de navegación inferior (fija)
        ClienteBottomNavBar(selected = 1, onSelect = {
            when (it) {
                0 -> navController.navigate(AppRoutes.Client.DASHBOARD)
                1 -> navController.navigate(AppRoutes.Client.CATALOG)
                2 -> navController.navigate(AppRoutes.Client.ORDERS)
                3 -> navController.navigate(AppRoutes.Client.CONFIG)
            }
        })
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun I_C1_VerCatalogoPreview() {
    val navController = rememberNavController()
    I_C1_VerCatalogo(navController = navController)
}

