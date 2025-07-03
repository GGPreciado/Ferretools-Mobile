package com.example.ferretools.ui.compra

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ferretools.R
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.ui.components.AdminBottomNavBar
import com.example.ferretools.ui.components.SelectorOpciones
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.detalles_cv.CampoFechaSeleccion
import com.example.ferretools.ui.components.detalles_cv.ListaProductosSeleccionados
import com.example.ferretools.viewmodel.compra.CompraViewModel
import com.example.ferretools.viewmodel.inventario.ListaProductosViewModel
import com.example.ferretools.model.database.Producto
import com.example.ferretools.model.enums.MetodosPago
import androidx.compose.runtime.collectAsState
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun C_02_ResumenCarritoCompra(
    navController: NavController,
    viewModel: CompraViewModel,
    listaProductosViewModel: ListaProductosViewModel = viewModel()
) {
    val productosUiState = listaProductosViewModel.uiState.collectAsState().value
    val uiState by viewModel.uiState.collectAsState()
    var bannerMessage by remember { mutableStateOf("") }
    var showBanner by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // No se necesita llamar a actualizarProductosConDetalles desde la UI, el ViewModel lo maneja internamente

    // Banner superior para advertencias
    if (showBanner) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFE082), RoundedCornerShape(0.dp))
                .padding(vertical = 8.dp)
                .zIndex(1f),
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

    // Navegación automática tras compra exitosa
    LaunchedEffect(uiState.status) {
        if (uiState.status == com.example.ferretools.viewmodel.compra.CompraUiState.Status.Success) {
            navController.navigate(com.example.ferretools.navigation.AppRoutes.Purchase.SUCCESS) {
                popUpTo(com.example.ferretools.navigation.AppRoutes.Purchase.CART) { inclusive = true }
            }
            viewModel.resetState() // Limpia el carrito tras éxito
        }
    }

    Scaffold(
        topBar = { TopNavBar(navController, "Detalles de compra") },
        bottomBar = { AdminBottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                // Fecha de venta (puedes personalizar con un selector de fecha real)
                Text("Fecha de compra")
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
                    seleccionado = uiState.metodoPago.name
                ) { seleccion ->
                    viewModel.cambiarMetodoPago(if (seleccion == "Efectivo") MetodosPago.Efectivo else MetodosPago.Yape)
                    Log.d("C_02_ResumenCarritoCompra", "Método de pago seleccionado: ${uiState.metodoPago}")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider(Modifier.padding(vertical = 18.dp))
            }
            // Lista de productos seleccionados en el carrito (scrollable dentro del LazyColumn)
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
                                    viewModel.cambiarCantidad(item.producto_id ?: "", nuevaCantidad, producto?.precio ?: 0.0)
                                    Log.d("C_02_ResumenCarritoCompra", "Disminuyó cantidad de ${producto?.nombre}")
                                }
                            }) { Text("-") }
                            OutlinedTextField(
                                value = cantidadTexto,
                                onValueChange = { newValue ->
                                    if (newValue.all { it.isDigit() }) {
                                        cantidadTexto = newValue
                                        val nuevaCantidad = newValue.toIntOrNull() ?: 0
                                        if (nuevaCantidad in 1..(producto?.cantidad_disponible ?: Int.MAX_VALUE)) {
                                            viewModel.cambiarCantidad(item.producto_id ?: "", nuevaCantidad, producto?.precio ?: 0.0)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .width(60.dp)
                                    .padding(horizontal = 8.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                )
                            )
                            Button(onClick = {
                                if ((item.cantidad ?: 0) >= (producto?.cantidad_disponible ?: 0)) {
                                    bannerMessage = "No hay suficiente stock para agregar más de este producto."
                                    showBanner = true
                                    Log.d("C_02_ResumenCarritoCompra", "Intento de aumentar más allá del stock: ${producto?.nombre}")
                                } else {
                                    val nuevaCantidad = (item.cantidad ?: 0) + 1
                                    cantidadTexto = nuevaCantidad.toString()
                                    viewModel.cambiarCantidad(item.producto_id ?: "", nuevaCantidad, producto?.precio ?: 0.0)
                                    Log.d("C_02_ResumenCarritoCompra", "Aumentó cantidad de ${producto?.nombre}")
                                }
                            }) { Text("+") }
                            Button(
                                onClick = {
                                    viewModel.quitarProducto(item.producto_id ?: "")
                                    Log.d("C_02_ResumenCarritoCompra", "Eliminado del carrito: ${producto?.nombre}")
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            ) { Text("Eliminar") }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(52.dp))
                Divider()
                // Total calculado
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total")
                    Text("S/ ${uiState.total}")
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Botón para confirmar la compra
                Button(
                    onClick = {
                        Log.d("C_02_ResumenCarritoCompra", "Compra confirmada. Productos: ${uiState.productosSeleccionados}")
                        viewModel.registrarCompra()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
                ) {
                    Text("Confirmar Compra", color = Color.Black)
                }
            }
        }
    }
}

// Dummy ViewModel solo para preview
class DummyCompraViewModel : androidx.lifecycle.ViewModel() {
    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(
        com.example.ferretools.viewmodel.compra.CompraUiState(
            productosSeleccionados = listOf(
                com.example.ferretools.model.database.ItemUnitario(
                    producto_id = "1",
                    cantidad = 3,
                    subtotal = 15.0
                )
            ),
            productosConDetalles = listOf(
                com.example.ferretools.model.database.ItemUnitario(
                    producto_id = "1",
                    cantidad = 3,
                    subtotal = 15.0
                ) to com.example.ferretools.model.database.Producto(
                    producto_id = "1",
                    nombre = "Producto Demo",
                    precio = 5.0,
                    cantidad_disponible = 20
                )
            ),
            metodoPago = com.example.ferretools.model.enums.MetodosPago.Efectivo,
            total = 15.0
        )
    )
    val uiState: kotlinx.coroutines.flow.StateFlow<com.example.ferretools.viewmodel.compra.CompraUiState> = _uiState
    // Métodos dummy para evitar errores de llamada en la UI
    fun cambiarCantidad(productoId: String, nuevaCantidad: Int, precio: Double) {}
    fun quitarProducto(productoId: String) {}
    fun cambiarMetodoPago(metodo: com.example.ferretools.model.enums.MetodosPago) {}
    fun registrarCompra() {}
}
/*
@Preview(showBackground = true)
@Composable
fun C_02_ResumenCarritoCompraPreview() {
    val navController = rememberNavController()
    val dummyViewModel = DummyCompraViewModel()
    C_02_ResumenCarritoCompra(navController = navController, viewModel = dummyViewModel)
}

 */