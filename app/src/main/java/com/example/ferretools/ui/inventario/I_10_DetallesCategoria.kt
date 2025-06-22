package com.example.ferretools.ui.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes

@Composable
fun I_10_DetallesCategoria(
    navController: NavController,
    categoria: String = "Nombre de la categoria"
) {
    var searchQuery by remember { mutableStateOf("") }
    val productosMock = listOf(
        "Producto 01",
        "Producto 02",
        "Producto 03",
        "Producto 05",
        "Producto ..."
    )

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
                text = "Ver Categorias",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Nombre de la categoría
        Text(
            text = categoria,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
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
                placeholder = { Text("Nombre producto") },
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

        // Lista de productos mock
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            productosMock.forEach { producto ->
                Button(
                    onClick = {
                        // Datos mock para ejemplo
                        navController.navigate(
                            AppRoutes.Inventory.PRODUCT_DETAILS
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(producto, fontWeight = FontWeight.Bold, color = Color.Black)
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
    I_10_DetallesCategoria(navController = navController)
}