package com.example.ferretools.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ferretools.R
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.utils.SesionUsuario

@Composable
fun ConfirmationNavBar(
    navController: NavController,
    onReceiptClick: () -> Unit,
    onNewOperationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(containerColor = Color(0xFF00BF59)) {
        NavigationBarItem(
            selected = false,
            onClick = onReceiptClick,
            icon = { Image(painterResource(R.drawable.documento), contentDescription = "Recibo") },
            label = { Text("Recibo", fontSize = 14.sp) },
            modifier = modifier
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                when (SesionUsuario.usuario?.rol) {
                    RolUsuario.ADMIN -> navController.navigate(AppRoutes.Admin.DASHBOARD)
                    RolUsuario.ALMACENERO -> navController.navigate(AppRoutes.Employee.DASHBOARD)
                    RolUsuario.CLIENTE -> navController.navigate(AppRoutes.Client.DASHBOARD)
                    else -> navController.navigate(AppRoutes.Admin.DASHBOARD)
                }
            },
            icon = { Image(painterResource(R.drawable.inicio), contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 14.sp) },
            modifier = modifier
        )
        NavigationBarItem(
            selected = false,
            onClick = onNewOperationClick,
            icon = { Image(painterResource(R.drawable.nuevo), contentDescription = "Nuevo", modifier = Modifier.size(70.dp)) },
            label = { Text("Nueva Operación", fontSize = 14.sp) },
            modifier = modifier
        )
    }
}