package com.example.ferretools.ui.venta

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.example.ferretools.ui.components.ConfirmationNavBar
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.R


@Composable
fun V_04_VentaExitosa(
    navController: NavController,

) {
    Scaffold(
        bottomBar = {
            ConfirmationNavBar(
                navController = navController,
                onReceiptClick = { navController.navigate(AppRoutes.Sale.RECEIPT) },
                onNewOperationClick = { navController.navigate(AppRoutes.Sale.CART) }
            )
        }
    ) { padding ->
        Box(

            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.7f)
                    .background(Color(0xFFB2FF59), RoundedCornerShape(80.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.venta_venta), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.venta_exitosa), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(Icons.Default.ThumbUp, contentDescription = stringResource(R.string.venta_exito_icon), modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun V_04_VentaExitosaPreview() {
    val navController = rememberNavController()
    V_04_VentaExitosa(navController = navController)
}