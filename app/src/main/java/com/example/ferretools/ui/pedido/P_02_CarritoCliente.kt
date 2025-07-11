package com.example.ferretools.ui.pedido

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ferretools.R
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.AdminBottomNavBar
import com.example.ferretools.ui.components.SelectorOpciones
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.detalles_cv.CampoFechaSeleccion
import com.example.ferretools.viewmodel.pedido.PedidoViewModel
import com.example.ferretools.model.enums.MetodosPago
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp

@Composable
fun P_02_CarritoCliente(
    navController: NavController,
    viewModel: PedidoViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var bannerMessage by remember { mutableStateOf("") }
    var showBanner by remember { mutableStateOf(false) }

    if (showBanner) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(vertical = 8.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(bannerMessage, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        LaunchedEffect(bannerMessage) {
            delay(2000)
            showBanner = false
        }
    }

    // Navegación automática tras pedido exitoso
    LaunchedEffect(uiState.status) {
        if (uiState.status == com.example.ferretools.viewmodel.pedido.PedidoUiState.Status.Success) {
            navController.navigate(AppRoutes.Order.SUCCESS) {
                popUpTo(AppRoutes.Order.CART) { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = { TopNavBar(navController, "Detalles de pedido") },
        //bottomBar = { AdminBottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                // Fecha de pedido
                Text("Fecha de pedido")
                CampoFechaSeleccion()
                Text(
                    "DD/MM/YYYY",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Medio de pago", Modifier.padding(vertical = 8.dp))

                // Medio de pago
                SelectorOpciones(
                    opcion1 = "Efectivo",
                    opcion2 =  "Yape",
                    opcion2Img = R.drawable.yape,
                    seleccionado = if (uiState.metodoPago == MetodosPago.Efectivo) "Efectivo" else "Yape"
                ) { seleccion ->
                    viewModel.cambiarMetodoPago(if (seleccion == "Efectivo") MetodosPago.Efectivo else MetodosPago.Yape)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider(Modifier.padding(vertical = 18.dp))
            }
            items(uiState.productosConDetalles) { (item, producto) ->
                var cantidadTexto by remember { mutableStateOf((item.cantidad ?: 0).toString()) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 2.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(producto?.nombre ?: "Producto", fontWeight = FontWeight.Bold)
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    "S/ ${producto?.precio ?: 0.0}", 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                                val cantidad = item.cantidad ?: 0
                                val subtotal = (producto?.precio ?: 0.0) * cantidad
                                Text(
                                    "Subtotal: S/ ${String.format("%.2f", subtotal)}",
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Button(onClick = {
                                if ((item.cantidad ?: 0) > 1) {
                                    val nuevaCantidad = (item.cantidad ?: 0) - 1
                                    cantidadTexto = nuevaCantidad.toString()
                                    viewModel.actualizarCantidadProducto(item.producto_id ?: "", nuevaCantidad)
                                }
                            }) { Text("-") }
                            OutlinedTextField(
                                value = cantidadTexto,
                                onValueChange = {
                                    cantidadTexto = it
                                    val nuevaCantidad = it.toIntOrNull() ?: 1
                                    if (nuevaCantidad > 0) {
                                        val stockDisponible = producto?.cantidad_disponible ?: 0
                                        if (nuevaCantidad <= stockDisponible) {
                                            viewModel.actualizarCantidadProducto(item.producto_id ?: "", nuevaCantidad)
                                        } else {
                                            bannerMessage = "No hay suficiente stock para este producto."
                                            showBanner = true
                                        }
                                    }
                                },
                                modifier = Modifier.width(60.dp).padding(horizontal = 8.dp),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                            )
                            Button(onClick = {
                                val nuevaCantidad = (item.cantidad ?: 0) + 1
                                val stockDisponible = producto?.cantidad_disponible ?: 0
                                if (nuevaCantidad <= stockDisponible) {
                                    cantidadTexto = nuevaCantidad.toString()
                                    viewModel.actualizarCantidadProducto(item.producto_id ?: "", nuevaCantidad)
                                } else {
                                    bannerMessage = "No hay suficiente stock para este producto."
                                    showBanner = true
                                }
                            }) { Text("+") }
                            Spacer(modifier = Modifier.width(16.dp))
                            // Botón de eliminar con ícono de basura
                            IconButton(
                                onClick = {
                                    viewModel.eliminarProducto(item.producto_id ?: "")
                                    Log.d("P_02_CarritoCliente", "Producto eliminado: ${producto?.nombre}")
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.Red,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text("S/ ${String.format("%.2f", uiState.total)}", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Botón Confirmar Pedido
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                    enabled = uiState.productosSeleccionados.isNotEmpty()
                ) {
                    Text("Confirmar Pedido", color = Color.Black)
                }
            }
        }
        // Diálogo de confirmación
        if (showConfirmDialog) {
            P_03_ConfirmarPedido(
                navController = navController,
                onConfirm = {
                    showConfirmDialog = false
                    viewModel.registrarPedido()
                },
                onDismiss = {
                    showConfirmDialog = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun P_02_CarritoClientePreview() {
    val navController = rememberNavController()
    val viewModel = remember { PedidoViewModel() }
    P_02_CarritoCliente(navController = navController, viewModel = viewModel)
}