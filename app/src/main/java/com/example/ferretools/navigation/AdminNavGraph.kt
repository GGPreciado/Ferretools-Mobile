package com.example.ferretools.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.toRoute

import com.example.ferretools.ui.home.HOME_Admin
import com.example.ferretools.ui.balance.*

import com.example.ferretools.ui.inventario.*

import com.example.ferretools.ui.compra.*

import com.example.ferretools.ui.venta.*

import com.example.ferretools.ui.inventario.I_04_DetallesProducto
import com.example.ferretools.ui.inventario.I_06_EditarProducto

import com.example.ferretools.viewmodel.inventario.AgregarProductoViewModel
import com.example.ferretools.viewmodel.inventario.ListaCategoriasViewModel
import com.example.ferretools.viewmodel.inventario.DetallesProductoViewModel
import com.example.ferretools.viewmodel.inventario.EditarProductoViewModel
import com.example.ferretools.viewmodel.inventario.ReporteInventarioViewModel
import com.example.ferretools.viewmodel.inventario.ListaProductosViewModel

fun NavGraphBuilder.adminNavGraph(navController: NavHostController) {
    navigation(
        startDestination = AppRoutes.Admin.DASHBOARD,
        route = "admin"
    ) {
        // Dashboard
        composable(AppRoutes.Admin.DASHBOARD) {
            HOME_Admin(navController = navController)
        }

        // Balance Stack
        composable(AppRoutes.Balance.LIST) {
            B_01_Balances(navController = navController)
        }
        composable(AppRoutes.Balance.DETAILS) {
            B_02_Detalles(navController = navController)
        }
        composable(AppRoutes.Balance.REPORT) {
            B_03_Reporte(navController = navController)
        }

        // Inventario Stack
        composable(AppRoutes.Inventory.LIST_PRODUCTS) {
            I_01_ListaProductos(navController = navController)
        }
        composable(AppRoutes.Inventory.ADD_PRODUCT) {
            val viewModel: AgregarProductoViewModel = viewModel()
            //val categoriaViewModel: ListaCategoriasViewModel = viewModel()
            I_02_AgregarProducto(
                navController = navController,
                viewModel = viewModel
                //categoriaViewModel = categoriaViewModel
            )
        }

        composable<AppRoutes.Inventory.PRODUCT_DETAILS> { backStackEntry ->
            val args: AppRoutes.Inventory.PRODUCT_DETAILS = backStackEntry.toRoute()
            val viewModel: DetallesProductoViewModel = viewModel()
            I_04_DetallesProducto(
                navController = navController,
                productoId = args.productoId,
                viewModel = viewModel
            )
        }

        composable<AppRoutes.Inventory.EDIT_PRODUCT> { backStackEntry ->
            val args: AppRoutes.Inventory.EDIT_PRODUCT = backStackEntry.toRoute()
            val viewModel: EditarProductoViewModel = viewModel()
            val categoriaViewModel: ListaCategoriasViewModel = viewModel()
            I_06_EditarProducto(
                navController = navController,
                productoId = args.productoId,
                viewModel = viewModel,
                categoriaViewModel = categoriaViewModel
            )
        }

        composable(AppRoutes.Inventory.LIST_CATEGORIES) {
            I_08_ListaCategorias(navController = navController)
        }
        composable(AppRoutes.Inventory.ADD_CATEGORY) {
            I_09_CrearCategoria(navController = navController)
        }
        composable(
            route = AppRoutes.Inventory.CATEGORY_DETAILS,
            arguments = listOf(
                navArgument("categoriaId") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getString("categoriaId") ?: ""
            val productosViewModel: ListaProductosViewModel = viewModel()
            val categoriaViewModel: ListaCategoriasViewModel = viewModel()
            I_10_DetallesCategoria(
                navController = navController,
                categoriaId = categoriaId,
                productosViewModel = productosViewModel,
                categoriaViewModel = categoriaViewModel
            )
        }
        composable(AppRoutes.Inventory.INVENTORY_REPORT) {
            val reporteViewModel: ReporteInventarioViewModel = viewModel()
            I_12_ReporteInventario(
                navController = navController,
                reporteViewModel = reporteViewModel
            )
        }

        // Compras
        composable(AppRoutes.Purchase.CART) {
            C_01_CarritoCompra(navController = navController)
        }
        composable(AppRoutes.Purchase.CART_SUMMARY) {
            C_02_ResumenCarritoCompra(navController = navController)
        }
        composable(AppRoutes.Purchase.SUCCESS) {
            C_04_CompraExitosa(navController = navController)
        }
        composable(AppRoutes.Purchase.RECEIPT) {
            C_05_BoletaCompra(navController = navController)
        }

        // Ventas
        composable(AppRoutes.Sale.CART) {
            V_01_CarritoVenta(navController = navController)
        }
        composable(AppRoutes.Sale.CART_SUMMARY) {
            V_02_ResumenCarritoVenta(navController = navController)
        }
        composable(AppRoutes.Sale.SUCCESS) {
            V_04_VentaExitosa(navController = navController)
        }
        composable(AppRoutes.Sale.RECEIPT) {
            V_05_BoletaVenta(navController = navController)
        }
    }
}
