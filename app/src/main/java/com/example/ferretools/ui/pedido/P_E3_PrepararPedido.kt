@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ferretools.ui.pedido

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.viewmodel.pedido.PedidoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import com.example.ferretools.R

private val GreenPrimary = Color(0xFF22D366)
private val GreenSuccess = Color(0xFF00BF59)
private val BackgroundColor = Color(0xFFF8F8F8)
private val TextPrimary = Color(0xFF333333)

@Composable
fun P_E3_PrepararPedido(
    navController: NavController,
    pedidoId: String,
    onPedidoPreparado: (() -> Unit)? = null,
    viewModel: PedidoViewModel = viewModel()
) {
    var preparado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preparar Pedido") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.LocalShipping, contentDescription = stringResource(R.string.pedido_atras), tint = Color.Black)
                    }
                }
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!preparado) {
                Text(
                    stringResource(R.string.pedido_confirmar_preparado, pedidoId),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        viewModel.prepararPedido(pedidoId)
                        preparado = true
                        onPedidoPreparado?.invoke()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.pedido_marcar_preparado), style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            } else {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = GreenSuccess,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    stringResource(R.string.pedido_marcado_preparado),
                    style = MaterialTheme.typography.titleLarge,
                    color = GreenSuccess,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        navController.popBackStack() // O navega a otra pantalla si lo deseas
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text(stringResource(R.string.pedido_volver), color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewP_E3_PrepararPedido() {
    val navController = rememberNavController()
    P_E3_PrepararPedido(
        navController = navController,
        pedidoId = "003"
    )
}