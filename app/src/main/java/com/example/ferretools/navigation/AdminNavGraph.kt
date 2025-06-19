package com.example.ferretools.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.NavHostController

import com.example.ferretools.ui.home.HOME_Admin
import com.example.ferretools.ui.balance.*

import com.example.ferretools.ui.inventario.*

import com.example.ferretools.ui.compra.*

import com.example.ferretools.ui.venta.*

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
            val viewModel: ProductoViewModel = viewModel()
            val firestoreViewModel: InventarioFirestoreViewModel = viewModel()
            I_02_AgregarProducto(
                navController = navController,
                viewModel = viewModel,
                firestoreViewModel = firestoreViewModel
            )
        }
        composable(AppRoutes.Inventory.PRODUCT_DETAILS) {
            I_05_ReporteProducto(navController = navController)
        }
        composable(AppRoutes.Inventory.LIST_CATEGORIES) {
            I_08_ListaCategorias(navController = navController)
        }
        composable(AppRoutes.Inventory.ADD_CATEGORY) {
            I_09_CrearCategoria(navController = navController)
        }
        composable(AppRoutes.Inventory.CATEGORY_DETAILS) {
            I_10_DetallesCategoria(navController = navController)
        }
        composable(AppRoutes.Inventory.INVENTORY_REPORT) {
            I_12_ReporteInventario(navController = navController)
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
