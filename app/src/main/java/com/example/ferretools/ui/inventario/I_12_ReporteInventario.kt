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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@Composable
fun I_12_ReporteInventario(
    navController: NavController? = null,
    reporteViewModel: ReporteInventarioViewModel = viewModel()
) {
    var showModal by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Todas las categorías") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val uiState = reporteViewModel.uiState.collectAsState().value
    val context = LocalContext.current

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
            IconButton(
                onClick = { showModal = true },
                enabled = !uiState.isGeneratingReport
            ) {
                if (uiState.isGeneratingReport) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Gray
                    )
                } else {
                    Icon(Icons.Default.MoreVert, contentDescription = "Elige un formato para descargar")
                }
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
        Dialog(onDismissRequest = { if (!uiState.isGeneratingReport) showModal = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text("Opciones de Reporte", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    
                    // Opción PDF
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { 
                                if (!uiState.isGeneratingReport) {
                                    reporteViewModel.generarReportePDF(
                                        context = context,
                                        onSuccess = { mensaje ->
                                            successMessage = mensaje
                                            showSuccessDialog = true
                                            showModal = false
                                        },
                                        onError = { error ->
                                            errorMessage = error
                                            showErrorDialog = true
                                            showModal = false
                                        }
                                    )
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.isGeneratingReport) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Gray
                            )
                        } else {
                            Text("📄", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Descargar PDF",
                            fontWeight = FontWeight.Medium,
                            color = if (uiState.isGeneratingReport) Color.Gray else Color.Black
                        )
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    // Opción Excel
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { 
                                if (!uiState.isGeneratingReport) {
                                    reporteViewModel.generarReporteExcel(
                                        context = context,
                                        onSuccess = { mensaje ->
                                            successMessage = mensaje
                                            showSuccessDialog = true
                                            showModal = false
                                        },
                                        onError = { error ->
                                            errorMessage = error
                                            showErrorDialog = true
                                            showModal = false
                                        }
                                    )
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.isGeneratingReport) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Gray
                            )
                        } else {
                            Text("📊", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Descargar Excel",
                            fontWeight = FontWeight.Medium,
                            color = if (uiState.isGeneratingReport) Color.Gray else Color.Black
                        )
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    // Opción Compartir
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { 
                                if (!uiState.isGeneratingReport) {
                                    reporteViewModel.compartirReporte(
                                        onSuccess = { contenido ->
                                            // Compartir usando Intent
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, contenido)
                                                putExtra(Intent.EXTRA_SUBJECT, "Reporte de Inventario")
                                            }
                                            context.startActivity(Intent.createChooser(intent, "Compartir reporte"))
                                            showModal = false
                                        },
                                        onError = { error ->
                                            errorMessage = error
                                            showErrorDialog = true
                                            showModal = false
                                        }
                                    )
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.isGeneratingReport) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Gray
                            )
                        } else {
                            Text("📤", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Compartir",
                            fontWeight = FontWeight.Medium,
                            color = if (uiState.isGeneratingReport) Color.Gray else Color.Black
                        )
                    }
                }
            }
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        Dialog(onDismissRequest = { showSuccessDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Éxito", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(successMessage, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { showSuccessDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }

    // Diálogo de error
    if (showErrorDialog) {
        Dialog(onDismissRequest = { showErrorDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("❌", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Error", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Red)
                    Spacer(Modifier.height(8.dp))
                    Text(errorMessage, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { showErrorDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Aceptar")
                    }
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