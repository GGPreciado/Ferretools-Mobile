package com.example.ferretools.ui.compra

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.boleta.BoletaNavBar
import com.example.ferretools.ui.components.boleta.DetalleProductoFila
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ferretools.viewmodel.compra.CompraViewModel
import com.example.ferretools.viewmodel.inventario.ListaProductosViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import java.text.SimpleDateFormat
import java.util.Locale
import android.util.Log
import androidx.compose.ui.res.stringResource
import com.example.ferretools.R

@Composable
fun C_05_BoletaCompra(
    navController: NavController,
    viewModel: CompraViewModel,
    listaProductosViewModel: ListaProductosViewModel = viewModel()
) {
    val compra = viewModel.ultimaCompraExitosa
    if (compra == null) {
        // Mostrar mensaje de error o pantalla vacía
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.compra_boleta_no_info), color = Color.Red)
        }
        return
    }
    val fechaCompra = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(compra.fecha.toDate())
    val metodoPago = compra.metodoPago.name
    Scaffold(
        topBar = { TopNavBar(navController, stringResource(R.string.compra_boleta_titulo)) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            // Caja principal con detalles de la compra
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(400.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Encabezados de la boleta
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(stringResource(R.string.compra_fecha), fontSize = 14.sp)
                            Text(stringResource(R.string.compra_medio_pago), fontSize = 14.sp)
                        }
                        Column {
                            Text(fechaCompra, fontSize = 14.sp)
                            Text(metodoPago, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.compra_productos), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    // Tabla de productos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.compra_nombre), fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.compra_cantidad), fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.compra_precio), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Divider()
                    // Lista de productos comprados
                    compra.productosConDetalles.forEach { (item, producto) ->
                        Log.d("C_05_BoletaCompra", "Producto en boleta: ${producto?.nombre}, cantidad: ${item.cantidad}")
                        val precioUnitario = producto?.precio ?: 0.0
                        val cantidad = item.cantidad ?: 0
                        val subtotal = precioUnitario * cantidad
                        
                        DetalleProductoFila(
                            nombre = producto?.nombre ?: stringResource(R.string.compra_producto),
                            cantidad = cantidad.toString(),
                            precio = stringResource(R.string.compra_s_total, subtotal)
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    // Total de la compra
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.compra_total), fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.compra_s_total, compra.total), fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            // Acciones de la boleta (volver, compartir, etc.)
            BoletaNavBar(navController)
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun C_05_BoletaCompraPreview() {
    val navController = rememberNavController()
    C_05_BoletaCompra(navController = navController)
}

 */