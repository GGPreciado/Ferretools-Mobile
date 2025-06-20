package com.example.ferretools.ui.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ferretools.model.database.Categoria
import com.example.ferretools.model.database.Producto
import com.example.ferretools.ui.inventario.ProductoSeleccionadoManager

@Composable
fun I_10_DetallesCategoria(
    navController: NavController,
    categoriaId: String,
    inventarioViewModel: InventarioFirestoreViewModel = viewModel(),
    categoriaViewModel: CategoriaFirestoreViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val productos = inventarioViewModel.productos.collectAsState().value
    val categorias = categoriaViewModel.categorias.collectAsState().value
    
    // Filtrar productos por categoría
    val productosFiltrados = productos.filter { it.categoria_id == categoriaId }
    val categoria = categorias.find { it.id == categoriaId }
    
    // Logs de depuración
    println("DEBUG: categoriaId recibido: $categoriaId")
    println("DEBUG: Total de productos: ${productos.size}")
    println("DEBUG: Productos filtrados: ${productosFiltrados.size}")
    println("DEBUG: Categoría encontrada: ${categoria?.nombre}")
    productos.forEach { producto ->
        println("DEBUG: Producto '${producto.nombre}' tiene categoria_id: '${producto.categoria_id}'")
    }
    
    // Filtrar por búsqueda si hay query
    val productosMostrados = if (searchQuery.isNotEmpty()) {
        productosFiltrados.filter { it.nombre.contains(searchQuery, ignoreCase = true) }
    } else {
        productosFiltrados
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header fijo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF22D366))
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
            }
            Text(
                text = "Detalles de Categoría",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Contenido desplazable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
        // Nombre de la categoría
        Text(
                text = categoria?.nombre ?: "Categoría no encontrada",
            fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
        )

            // Contador de productos
            Text(
                text = "${productosFiltrados.size} productos en esta categoría",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
            )

            // Valor total de productos en la categoría
            val valorTotal = productosFiltrados.sumOf { it.precio * it.cantidad_disponible }
            Text(
                text = "Valor total: S/ ${valorTotal}",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 24.dp, bottom = 24.dp)
            )

        // Buscador
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(Color(0xFFEDE7F6), RoundedCornerShape(24.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Menu,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar producto") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.Gray,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de productos reales
            if (productosMostrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No se encontraron productos" else "No hay productos en esta categoría",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    productosMostrados.forEach { producto ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    ProductoSeleccionadoManager.seleccionarProducto(producto)
                                    navController.navigate(AppRoutes.Inventory.PRODUCT_DETAILS)
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Producto",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        producto.nombre,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        "S/ ${producto.precio} - Stock: ${producto.cantidad_disponible}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductosCategoriaScreen() {
    val navController = rememberNavController()
    I_10_DetallesCategoria(
        navController = navController,
        categoriaId = "test-id"
    )
}