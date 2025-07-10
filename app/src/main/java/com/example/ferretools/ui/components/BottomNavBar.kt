package com.example.ferretools.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.ferretools.R

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AdminBottomNavBar(navController: NavController, modifier: Modifier = Modifier) {
    NavigationBar(containerColor = Color(0xFF00BF59)) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(AppRoutes.Admin.DASHBOARD) },
            icon = { Image(painterResource(R.drawable.inicio), contentDescription = "Inicio") },
            label = { Text("Inicio",
                fontSize = 16.sp, // Tamaño aumentado (original era ~12.sp)
                fontWeight = FontWeight.Bold // Opcional: para mejor legibilidad
            ) },
            modifier = modifier
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(AppRoutes.Balance.LIST) },
            icon = { Image(painterResource(R.drawable.documento), contentDescription = "Balance") },
            label = { Text("Balance",
                fontSize = 16.sp, // Tamaño aumentado (original era ~12.sp)
                fontWeight = FontWeight.Bold // Opcional: para mejor legibilidad
            ) },
            modifier = modifier
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(AppRoutes.Inventory.LIST_PRODUCTS) },
            icon = { Image(painterResource(R.drawable.inventario), contentDescription = "Inventario") },
            label = { Text("Inventario",
                fontSize = 16.sp, // Tamaño aumentado (original era ~12.sp)
                fontWeight = FontWeight.Bold // Opcional: para mejor legibilidad
            )},
            modifier = modifier
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(AppRoutes.Config.MAIN) },
            icon = { Image(painterResource(R.drawable.cuenta), contentDescription = "Cuenta") },
            label = { Text("Cuenta",
                fontSize = 16.sp, // Tamaño aumentado (original era ~12.sp)
                fontWeight = FontWeight.Bold // Opcional: para mejor legibilidad
            ) },
            modifier = modifier
        )
    }
}

@Composable
fun ClientBottomNavBar(navController: NavController, modifier: Modifier = Modifier) {
    NavigationBar(containerColor = Color(0xFF00BF59)) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(AppRoutes.Client.DASHBOARD) },
            icon = { Image(painterResource(R.drawable.inicio), contentDescription = "Inicio") },
            label = { Text("Inicio",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ) },
            modifier = modifier
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(AppRoutes.Client.CATALOG) },
            icon = { Image(painterResource(R.drawable.inventario), contentDescription = "Catálogo") },
            label = { Text("Catálogo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )},
            modifier = modifier
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(AppRoutes.Client.ORDERS) },
            icon = { Image(painterResource(R.drawable.documento), contentDescription = "Pedidos") },
            label = { Text("Pedidos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ) },
            modifier = modifier
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(AppRoutes.Client.CONFIG) },
            icon = { Image(painterResource(R.drawable.cuenta), contentDescription = "Cuenta") },
            label = { Text("Cuenta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ) },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun AdminBottomNavBarPreview() {
    // Simulamos un NavController para la preview
    val mockNavController = rememberNavController()

    AdminBottomNavBar(
        navController = mockNavController,
        modifier = Modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ClientBottomNavBarPreview() {
    // Simulamos un NavController para la preview
    val mockNavController = rememberNavController()

    ClientBottomNavBar(
        navController = mockNavController,
        modifier = Modifier
    )
}
