package com.example.ferretools.ui.venta

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.R
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.AdminBottomNavBar
import com.example.ferretools.ui.components.SelectorOpciones
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.detalles_cv.CampoFechaSeleccion
import com.example.ferretools.viewmodel.venta.VentaViewModel
import com.example.ferretools.viewmodel.inventario.ListaProductosViewModel
import kotlinx.coroutines.delay

@Composable
fun V_02_ResumenCarritoVenta(
    navController: NavController,
    viewModel: VentaViewModel,
    listaProductosViewModel: ListaProductosViewModel = viewModel()
) {
    val productosUiState = listaProductosViewModel.uiState.collectAsState().value
    val uiState by viewModel.uiState.collectAsState()
    var bannerMessage by remember { mutableStateOf("") }
    var showBanner by remember { mutableStateOf(false) }

    // Banner superior para advertencias
    if (showBanner) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFE082), RoundedCornerShape(0.dp))
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
        LaunchedEffect(bannerMessage) {
            delay(2000)
            showBanner = false
        }
    }

    // Navegación automática tras venta exitosa
    LaunchedEffect(uiState.status) {
        if (uiState.status == com.example.ferretools.viewmodel.venta.VentaUiState.Status.Success) {
            navController.navigate(AppRoutes.Sale.SUCCESS) {
                popUpTo(AppRoutes.Sale.CART) { inclusive = true }
            }
            viewModel.resetState() // Limpia el carrito tras éxito
        }
    }

    Scaffold(
        topBar = { TopNavBar(navController, "Detalles de venta") },
        bottomBar = { AdminBottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                // Fecha de venta
                Text("Fecha de venta")
                CampoFechaSeleccion()
                Text(
                    "DD/MM/YYYY",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Selección de método de pago
                Text("Medio de pago", Modifier.padding(vertical = 8.dp))
                SelectorOpciones(
                    opcion1 = "Efectivo",
                    opcion2 = "Yape",
                    opcion2Img = R.drawable.yape,
                    seleccionado = if (uiState.metodoPago == com.example.ferretools.model.enums.MetodosPago.Efectivo) "Efectivo" else "Yape"
                ) { seleccion ->
                    viewModel.cambiarMetodoPago(if (seleccion == "Efectivo") com.example.ferretools.model.enums.MetodosPago.Efectivo else com.example.ferretools.model.enums.MetodosPago.Yape)
                    Log.d("V_02_ResumenCarritoVenta", "Método de pago seleccionado: ${uiState.metodoPago}")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider(Modifier.padding(vertical = 18.dp))
            }
            // Lista de productos seleccionados en el carrito de venta
            itemsIndexed(uiState.productosConDetalles) { idx, (item, producto) ->
                var cantidadTexto by remember { mutableStateOf((item.cantidad ?: 0).toString()) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        // Fila superior: nombre y precio
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                producto?.nombre ?: "Producto",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "S/ ${producto?.precio ?: 0.0}",
                                color = Color.Gray,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        // Fila inferior: controles de cantidad y eliminar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = {
                                if ((item.cantidad ?: 0) > 1) {
                                    val nuevaCantidad = (item.cantidad ?: 0) - 1
                                    cantidadTexto = nuevaCantidad.toString()
                                    viewModel.actualizarCantidadProducto(item.producto_id ?: "", nuevaCantidad)
                                    Log.d("V_02_ResumenCarritoVenta", "Disminuyó cantidad de ${producto?.nombre}")
                                }
                            }) { Text("-") }
                            OutlinedTextField(
                                value = cantidadTexto,
                                onValueChange = {
                                    cantidadTexto = it
                                    val nuevaCantidad = it.toIntOrNull() ?: 1
                                    if (nuevaCantidad > 0) {
                                        // Validar stock disponible
                                        val stockDisponible = producto?.cantidad_disponible ?: 0
                                        if (nuevaCantidad <= stockDisponible) {
                                            viewModel.actualizarCantidadProducto(item.producto_id ?: "", nuevaCantidad)
                                        } else {
                                            bannerMessage = "No hay suficiente stock para este producto."
                                            showBanner = true
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .width(60.dp)
                                    .padding(horizontal = 8.dp),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                            )
                            Button(onClick = {
                                val nuevaCantidad = (item.cantidad ?: 0) + 1
                                val stockDisponible = producto?.cantidad_disponible ?: 0
                                if (nuevaCantidad <= stockDisponible) {
                                    cantidadTexto = nuevaCantidad.toString()
                                    viewModel.actualizarCantidadProducto(item.producto_id ?: "", nuevaCantidad)
                                    Log.d("V_02_ResumenCarritoVenta", "Aumentó cantidad de ${producto?.nombre}")
                                } else {
                                    bannerMessage = "No hay suficiente stock para este producto."
                                    showBanner = true
                                }
                            }) { Text("+") }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    viewModel.eliminarProducto(item.producto_id ?: "")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Eliminar", color = Color.White)
                            }
                        }
                    }
                }
            }
            item {
                Divider()
                // Total
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
                // Botón Confirmar Venta
                Button(
                    onClick = {
                        viewModel.registrarVenta()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                    enabled = uiState.productosSeleccionados.isNotEmpty()
                ) {
                    Text("Confirmar Venta", color = Color.Black)
                }
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun V_02_ResumenCarritoVentaPreview() {
    val navController = rememberNavController()
    V_02_ResumenCarritoVenta(
        navController = navController
    )
}

 */