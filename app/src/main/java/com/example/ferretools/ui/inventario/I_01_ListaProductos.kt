package com.example.ferretools.ui.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.AdminBottomNavBar
import com.example.ferretools.ui.components.SummaryCard
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ferretools.ui.inventario.InventarioFirestoreViewModel


@Composable
fun I_01_ListaProductos(
    navController: NavController,
    viewModel: InventarioFirestoreViewModel = viewModel()
) {
   val productos = viewModel.productos.collectAsState().value
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
           .background(MaterialTheme.colorScheme.background)
    ) {
        // Header (fijo)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF00E676))
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
                    text = "Nombre de Usuario", 
                    color = MaterialTheme.colorScheme.onPrimary, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nombre de la Tienda", 
                    color = MaterialTheme.colorScheme.onPrimary, 
                    fontSize = 13.sp
                )
            }
        }
        // Botón de reportes (fijo)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navController.navigate(AppRoutes.Inventory.INVENTORY_REPORT) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = "Reportes",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Reportes", 
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        // Contenido desplazable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            // Resumen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryCard(title = "Total de Productos", value = productos.size.toString())

                val valorTotal = productos.sumOf { it.precio * it.cantidad_disponible }
                SummaryCard(title = "Valor Total", value = "${valorTotal} PEN")
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Chips de categorías (puedes adaptar para que sean dinámicas)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { /* TODO */ },
                    label = { 
                        Text(
                            "Todas las categorías", 
                            color = MaterialTheme.colorScheme.onPrimary
                        ) 
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
                // Puedes agregar más chips dinámicamente aquí
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Lista de productos desde Firestore
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
               productos.forEach { producto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
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
        // Botones grandes (fijos)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 32.dp, vertical = 20.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate(AppRoutes.Inventory.ADD_PRODUCT) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Agregar producto", 
                    color = MaterialTheme.colorScheme.onPrimary, 
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate(AppRoutes.Inventory.LIST_CATEGORIES) },
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
        AdminBottomNavBar(navController)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun I_01_ListaProductosPreview() {
    val navController = rememberNavController()
    I_01_ListaProductos(navController = navController)
}

