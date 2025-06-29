package com.example.ferretools.ui.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.viewmodel.inventario.ReporteInventarioViewModel

@Composable
fun I_12_ReporteInventario(
    navController: NavController? = null,
    reporteViewModel: ReporteInventarioViewModel = viewModel()
) {
    var showModal by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Todas las categorías") }

    val uiState = reporteViewModel.uiState.collectAsState().value

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // TopBar
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFF00C853))
                .padding(vertical = 24.dp, horizontal = 8.dp)
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController?.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "Reporte de Stock",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        // Total de Productos y opciones
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Total de Productos", fontWeight = FontWeight.Bold)
                    Text(uiState.productosFiltrados.size.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { showModal = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Elige un formato para descargar")
            }
        }

        // Chips de categorías desplazables horizontalmente
        LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(uiState.categoriasName) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = {
                        selectedCategory = cat
                        if (selectedCategory == "Todas las categorías") {
                            reporteViewModel.filtrarPorCategoria("")
                        } else {
                            // Buscar el id de la categoría seleccionada por nombre
                            val categoriaId = uiState.categorias.find { it.nombre == selectedCategory }?.id
                            reporteViewModel.filtrarPorCategoria(categoriaId ?: "")
                        }
                    },
                    label = { Text(cat) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        // Lista de productos desplazable verticalmente
        LazyColumn(Modifier.padding(16.dp).weight(1f, fill = true)) {
            if (uiState.productosFiltrados.isEmpty()) {
                item {
                    Text("No hay productos para mostrar", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            } else {
                items(uiState.productosFiltrados) { producto ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(48.dp)
                                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(android.R.drawable.ic_menu_gallery),
                                    contentDescription = "Imagen",
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(producto.nombre, fontWeight = FontWeight.Bold)
                                Text("S/ ${producto.precio}")
                                Text("${producto.cantidad_disponible} disponibles", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal de opciones
    if (showModal) {
        Dialog(onDismissRequest = { showModal = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text("Opciones", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Descargar PDF",
                        Modifier
                            .fillMaxWidth()
                            .clickable { showModal = false }
                            .padding(8.dp)
                    )
                    Text(
                        "Descargar en Excel",
                        Modifier
                            .fillMaxWidth()
                            .clickable { showModal = false }
                            .padding(8.dp)
                    )
                    Text(
                        "Compartir",
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                showModal = false
                                navController?.navigate(AppRoutes.Inventory.SHARE_REPORT)
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun I_12_ReporteInventarioPreview() {
    val navController = rememberNavController()
    I_12_ReporteInventario(navController = navController)
}