package com.example.ferretools.ui.pedido

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.R

@Composable
fun P_03_ConfirmarPedido(
    navController: NavController,
    onConfirm: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = { onDismiss?.invoke() ?: navController.popBackStack() },
        title = {
            Text(text = stringResource(R.string.pedido_confirmar_titulo))
        },
        text = {
            Text(stringResource(R.string.pedido_confirmar_texto))
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm?.invoke() ?: navController.navigate(AppRoutes.Order.SUCCESS) {
                        popUpTo(AppRoutes.Order.CART) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BF59))
            ) {
                Text(stringResource(R.string.pedido_confirmar_si), color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss?.invoke() ?: navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
            ) {
                Text(stringResource(R.string.pedido_confirmar_cancelar), color = Color.White)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewP_03_ConfirmarPedido() {
    val navController = rememberNavController()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)), // Fondo semitransparente para simular modal
        contentAlignment = Alignment.Center
    ) {
        P_03_ConfirmarPedido(navController = navController)
    }
} 