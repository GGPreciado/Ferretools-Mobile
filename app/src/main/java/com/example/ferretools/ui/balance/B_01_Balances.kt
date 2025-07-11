package com.example.ferretools.ui.balance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.R
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.AdminBottomNavBar
import com.example.ferretools.ui.components.LoadingOverlay
import com.example.ferretools.ui.components.SelectorOpciones
import com.example.ferretools.ui.components.UserDataBar
import com.example.ferretools.ui.components.detalles_cv.CampoFechaSeleccion
import com.example.ferretools.viewmodel.HomeAdminViewModel
import com.example.ferretools.viewmodel.balance.BalanceViewModel
import com.example.ferretools.utils.ReportGenerator
import com.example.ferretools.utils.SesionUsuario
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

private val YellowPrimary = Color(0xFFFFEB3B)
private val GreenLight = Color(0xFFB9F6CA)
private val RedLight = Color(0xFFFF8A80)
private val GreenText = Color(0xFF22D366)
private val RedText = Color.Red
private val CardBorder = Color.Black



@Composable
fun B_01_Balances(
    navController: NavController,
    homeAdminViewModel: HomeAdminViewModel = viewModel(),
    balanceViewModel: BalanceViewModel = viewModel()
) {
    // Observar datos del usuario y negocio
    val userName = homeAdminViewModel.userName.collectAsState().value
    val storeName = homeAdminViewModel.storeName.collectAsState().value
    
    // Observar datos del balance
    val resumen = balanceViewModel.resumen.collectAsState().value
    val movimientos = balanceViewModel.movimientos.collectAsState().value
    val isLoading = balanceViewModel.isLoading.collectAsState().value
    val error = balanceViewModel.error.collectAsState().value
    val fechaSeleccionada = balanceViewModel.fechaSeleccionada.collectAsState().value
    
    var filtro by remember { mutableStateOf("Ingresos") }
    var isGeneratingPDF by remember { mutableStateOf(false) }
    var shouldGeneratePDF by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    // Permission launcher for storage permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // Permissions granted, proceed with PDF generation
            shouldGeneratePDF = true
        } else {
            android.widget.Toast.makeText(
                context,
                "Se requieren permisos de almacenamiento para generar el PDF. Ve a Configuración > Aplicaciones > Ferretools > Permisos",
                android.widget.Toast.LENGTH_LONG
            ).show()
            isGeneratingPDF = false
        }
    }
    
    // LaunchedEffect to handle PDF generation
    LaunchedEffect(shouldGeneratePDF) {
        if (shouldGeneratePDF) {
            shouldGeneratePDF = false
            isGeneratingPDF = true
            
            try {
                val fechaActual = dateFormatter.format(java.util.Date())
                
                // Validar que los datos estén disponibles
                if (resumen.total == 0.0 && resumen.ingresos == 0.0 && resumen.egresos == 0.0) {
                    android.widget.Toast.makeText(
                        context,
                        "No hay datos para generar el reporte",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    isGeneratingPDF = false
                    return@LaunchedEffect
                }
                
                val pdfContent = ReportGenerator.generarPDFBalance(
                    resumen = resumen,
                    movimientos = movimientos,
                    fecha = fechaActual,
                    negocioNombre = storeName ?: "Negocio"
                )
                
                val uri = ReportGenerator.guardarArchivo(
                    context = context,
                    contenido = pdfContent,
                    nombreArchivo = "balance_${fechaActual.replace("/", "_")}.pdf",
                    mimeType = "application/pdf"
                )
                
                uri?.let { fileUri ->
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        putExtra(Intent.EXTRA_SUBJECT, "Reporte de Balance - ${storeName ?: "Negocio"}")
                        putExtra(Intent.EXTRA_TEXT, "Reporte de balance generado el $fechaActual")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Compartir reporte"))
                } ?: run {
                    // Mostrar error si no se pudo guardar el archivo
                    android.widget.Toast.makeText(
                        context,
                        "Error al generar el PDF",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                // Mostrar error específico
                android.widget.Toast.makeText(
                    context,
                    "Error al generar PDF: ${e.message}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
                android.util.Log.e("BalanceScreen", "Error generando PDF", e)
            } finally {
                isGeneratingPDF = false
            }
        }
    }

    Scaffold(
        topBar = { UserDataBar(userName, storeName) },
        bottomBar = { AdminBottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Fecha", style = MaterialTheme.typography.titleMedium)
                Button(
                    onClick = { balanceViewModel.actualizarFecha(fechaSeleccionada) },
                    colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Actualizar", color = Color.Black, style = MaterialTheme.typography.bodySmall)
                }
            }
            CampoFechaSeleccion(
                fechaInicial = fechaSeleccionada,
                onFechaChange = { balanceViewModel.actualizarFecha(it) }
            )

            Text("Balance", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(2.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "S/ ${String.format("%.2f", resumen.total)}", 
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (resumen.total >= 0) GreenText else RedText
                        )
                    }
                    Divider(Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("Ingresos", color = GreenText, style = MaterialTheme.typography.bodyMedium)
                            Text("S/ ${String.format("%.2f", resumen.ingresos)}", color = GreenText, style = MaterialTheme.typography.bodyLarge)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("Egresos", color = RedText, style = MaterialTheme.typography.bodyMedium)
                            Text("S/ ${String.format("%.2f", resumen.egresos)}", color = RedText, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Divider(Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { navController.navigate(AppRoutes.Balance.DETAILS) },
                            colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
                        ) {
                            Text("Ver detalles", color = Color.Black)
                        }
                        Button(
                            onClick = { 
                                if (isGeneratingPDF) return@Button
                                
                                // Request permissions if not already granted
                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
                                    // For Android 10 and below, request WRITE_EXTERNAL_STORAGE
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        permissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                                    } else {
                                        shouldGeneratePDF = true
                                    }
                                } else {
                                    // For Android 11+ (API 30+), scoped storage is used, no need for WRITE_EXTERNAL_STORAGE
                                    shouldGeneratePDF = true
                                }
                            },
                            enabled = !isGeneratingPDF,
                            colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
                        ) {
                            if (isGeneratingPDF) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generando...", color = Color.Black)
                            } else {
                                Text("Convertir a PDF", color = Color.Black)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SelectorOpciones(
                opcion1 = "Ingresos",
                opcion2 = "Egresos",
                seleccionado = filtro
            ) { filtro = it }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                LoadingOverlay(show = true, message = "Cargando balance...")
            } else if (error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            error,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { 
                                balanceViewModel.limpiarError()
                                balanceViewModel.actualizarFecha(fechaSeleccionada)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
                        ) {
                            Text("Reintentar", color = Color.Black)
                        }
                    }
                }
            } else {
                if (SesionUsuario.usuario?.negocioId == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "No hay negocio asignado",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Contacta al administrador para asignar un negocio",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    ListaMovimientos(movimientos = balanceViewModel.filtrarMovimientos(filtro))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate(AppRoutes.Balance.REPORT) },
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 15.dp)
            ) {
                Text("Reporte", color = Color.Black)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate(AppRoutes.Sale.CART) },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenLight)
                ) {
                    Text("Agregar venta", color = Color.Black)
                }
                Button(
                    onClick = { navController.navigate(AppRoutes.Purchase.CART) },
                    colors = ButtonDefaults.buttonColors(containerColor = RedLight)
                ) {
                    Text("Agregar compra", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ListaMovimientos(movimientos: List<com.example.ferretools.viewmodel.balance.Movimiento>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        if (movimientos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay movimientos para mostrar.", 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = Color.Gray
                )
            }
        } else {
            movimientos.forEach { mov ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painterResource(R.drawable.inventario),
                            contentDescription = "Producto",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(mov.productos, style = MaterialTheme.typography.bodyMedium)
                        Text(mov.fecha, style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "S/ ${String.format("%.2f", mov.monto)}", 
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (mov.monto >= 0) GreenText else RedText
                        )
                        Text(mov.metodo, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewB_01_Balances() {
    val navController = rememberNavController()
    B_01_Balances(navController = navController)
}