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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.Icons
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext

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
    val context = LocalContext.current

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
        topBar = { TopNavBar(navController, stringResource(R.string.venta_detalles)) },
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
                Text(stringResource(R.string.venta_fecha))
                CampoFechaSeleccion()
                Text(
                    stringResource(R.string.venta_dd_mm_yyyy),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Selección de método de pago
                Text(stringResource(R.string.venta_medio_pago), Modifier.padding(vertical = 8.dp))
                val efectivoStr = stringResource(R.string.venta_efectivo)
                val yapeStr = stringResource(R.string.venta_yape)
                SelectorOpciones(
                    opcion1 = efectivoStr,
                    opcion2 = yapeStr,
                    opcion2Img = R.drawable.yape,
                    seleccionado = if (uiState.metodoPago == com.example.ferretools.model.enums.MetodosPago.Efectivo) efectivoStr else yapeStr
                ) { seleccion ->
                    viewModel.cambiarMetodoPago(if (seleccion == efectivoStr) com.example.ferretools.model.enums.MetodosPago.Efectivo else com.example.ferretools.model.enums.MetodosPago.Yape)
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
                                producto?.nombre ?: stringResource(R.string.venta_producto),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    stringResource(R.string.venta_s_precio, producto?.precio ?: 0.0),
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                val cantidad = item.cantidad ?: 0
                                val subtotal = (producto?.precio ?: 0.0) * cantidad
                                Text(
                                    "Subtotal: $ ${String.format("%.2f", subtotal)}",
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
                                            bannerMessage = context.getString(R.string.venta_no_suficiente_stock)
                                            showBanner = true
                                        }
                                    }
                                    // Logcat para trazar el cambio de cantidad
                                    Log.d("V_02_ResumenCarritoVenta", "Cantidad editada manualmente: $nuevaCantidad")
                                },
                                // Aumenté el ancho de 60.dp a 100.dp para permitir más dígitos
                                modifier = Modifier
                                    .width(100.dp)
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
                                    bannerMessage = context.getString(R.string.venta_no_suficiente_stock)
                                    showBanner = true
                                }
                            }) { Text("+") }
                            Spacer(modifier = Modifier.width(16.dp))
                            // Botón de eliminar con ícono de basura
                            IconButton(
                                onClick = {
                                    viewModel.eliminarProducto(item.producto_id ?: "")
                                    Log.d("V_02_ResumenCarritoVenta", "Producto eliminado: ${producto?.nombre}")
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.venta_eliminar),
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
                // Total
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.venta_total_label), fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.venta_s_total, String.format("%.2f", uiState.total)), fontWeight = FontWeight.Bold)
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
                    Text(stringResource(R.string.venta_confirmar), color = Color.Black)
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