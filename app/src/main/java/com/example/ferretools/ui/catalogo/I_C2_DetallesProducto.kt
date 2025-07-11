package com.example.ferretools.ui.catalogo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.R
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.viewmodel.inventario.DetallesProductoViewModel

@Composable
fun I_C2_DetallesProducto(
    navController: NavController,
    productoId: String,
    viewModel: DetallesProductoViewModel = viewModel()
) {
    // Cargar el producto por ID al entrar a la pantalla
    LaunchedEffect(productoId) {
        viewModel.cargarProductoPorId(productoId)
    }
    val producto = viewModel.producto.collectAsState().value
    val categoriaNombre = viewModel.categoriaNombre.collectAsState().value

    println("DEBUG: I_C2_DetallesProducto - Producto seleccionado: ${producto?.nombre}")

    if (producto == null) {
        // Mostrar mensaje si no hay producto seleccionado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No hay producto seleccionado",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Volver")
            }
        }
        return
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header fijo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF00E676))
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.Black
                )
            }
            Text(
                text = "Detalles de Producto",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
        
        // Contenido desplazable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Imagen del producto
            Box(
                modifier = Modifier
                    .size(width = 300.dp, height = 100.dp)
                    .background(Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.cargar_imagen),
                        contentDescription = "Imagen del producto",
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Imagen del producto",
                        color = Color.Black,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Campos de información del producto
            DetalleCampo("Código de barras", producto.codigo_barras)
            DetalleCampo("Nombre de Producto", producto.nombre)
            DetalleCampo("Precio", "S/ ${producto.precio}")
            DetalleCampo("Cantidad disponible", producto.cantidad_disponible.toString())
            DetalleCampo("Categoría", categoriaNombre ?: "Sin categoría")
            DetalleCampo("Descripción", producto.descripcion ?: "Sin descripción", multiline = true)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón de análisis (solo visualización)
//            Button(
//                onClick = {
//                    navController.navigate(
//                        AppRoutes.Inventory.PRODUCT_REPORT(
//                            productoId = producto.codigo_barras,
//                            productoNombre = producto.nombre
//                        )
//                    )
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
//            ) {
//                Text("Ver análisis del producto", color = Color.Black)
//            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetalleCampo(label: String, value: String, multiline: Boolean = false) {
    Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (multiline) 64.dp else 32.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Text(value)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun I_C2_DetallesProductoPreview() {
    val navController = rememberNavController()
    I_C2_DetallesProducto(
        navController = navController,
        productoId = "test-product-id"
    )
}

