package com.example.ferretools.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ferretools.ui.home.HOME_Admin
import com.example.ferretools.ui.inventario.I_01_ListaProductos
import com.example.ferretools.ui.inventario.I_08_ListaCategorias
import com.example.ferretools.ui.inventario.I_09_CrearCategoria
import com.example.ferretools.ui.inventario.I_10_DetallesCategoria
import com.example.ferretools.ui.inventario.I_12_ReporteInventario

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HOME_Admin(navController)
        }
        composable("inventario") {
            I_01_ListaProductos(navController)
        }
        composable("reporte") {
            I_12_ReporteInventario()
        }
        composable("categorias") {
            I_08_ListaCategorias(navController)
        }
        composable(
            route = "productos_categoria/{categoriaId}",
            arguments = listOf(
                androidx.navigation.navArgument("categoriaId") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getString("categoriaId") ?: ""
            I_10_DetallesCategoria(
                navController = navController,
                categoriaId = categoriaId
            )
        }
        composable("agregar_producto") {
//            I_02_AgregarProducto(navController)
        }
        composable("crear_categoria") {
            I_09_CrearCategoria(navController)
        }
    }
} 