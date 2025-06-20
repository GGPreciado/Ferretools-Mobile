package com.example.ferretools.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.ferretools.ui.compra.C_01_CarritoCompra
import com.example.ferretools.ui.compra.C_02_ResumenCarritoCompra
import com.example.ferretools.ui.compra.C_04_CompraExitosa
import com.example.ferretools.ui.compra.C_05_BoletaCompra
import com.example.ferretools.ui.home.HOME_Empleado
import com.example.ferretools.ui.home.PedidoPendiente
import com.example.ferretools.ui.inventario.I_01_ListaProductos
import com.example.ferretools.ui.inventario.I_08_ListaCategorias
import com.example.ferretools.ui.inventario.I_09_CrearCategoria
import com.example.ferretools.ui.inventario.I_10_DetallesCategoria
import com.example.ferretools.ui.venta.V_01_CarritoVenta
import com.example.ferretools.ui.venta.V_02_ResumenCarritoVenta
import com.example.ferretools.ui.venta.V_04_VentaExitosa
import com.example.ferretools.ui.venta.V_05_BoletaVenta

fun NavGraphBuilder.almaceneroNavGraph(navController: NavHostController) {
    navigation(
        startDestination = "HOME_Empleado",
        route = "almacenero"
    ) {
        // Pedidos
        composable("HOME_Empleado") {
            val pedidosDemo = listOf(
                PedidoPendiente("001", "Juan Pérez", "2024-06-10", listOf("Arroz x2", "Azúcar x1"), "Pendiente"),
                PedidoPendiente("002", "María López", "2024-06-11", listOf("Leche x3", "Pan x5"), "Pendiente")
            )

            HOME_Empleado(
                navController = navController,
                pedidosPendientes = pedidosDemo,
            )
        }
        //composable("P_E1_HistorialPedidos") { P_E1_HistorialPedidos(navController) }
        //composable("P_E2_DetallesPedido") { P_E2_DetallesPedido(navController) }
        //composable("P_E3_PrepararPedido") { P_E3_PrepararPedido(navController) }

        // Inventario
        composable("I_01_ListaProductos") { I_01_ListaProductos(navController) }
//        composable("I_02_AgregarProducto") { I_02_AgregarProducto(navController) }
        //composable("I_03_ProductoAgregado") { I_03_ProductoAgregado(navController) }
        // falta composable("I_04_DetallesProducto") { I_04_DetallesProducto(navController) }
//        composable("I_05_ReporteProducto") { I_05_ReporteProducto(navController) }
        // falta composable("I_06_EditarProducto") { I_06_EditarProducto(navController) }
        // falta composable("I_07_ConfirmarEliminarProducto") { I_07_ConfirmarEliminarProducto(navController) }
        composable("I_08_ListaCategorias") { I_08_ListaCategorias(navController) }
        composable("I_09_CrearCategoria") { I_09_CrearCategoria(navController) }
        composable("I_10_DetallesCategoria/{categoria}") { backStackEntry ->
            I_10_DetallesCategoria(
                navController = navController,
                categoria = backStackEntry.arguments?.getString("categoria") ?: ""
            )
        }
        //composable("I_11_CategoríaAgregada") { I_11_CategoríaAgregada(navController) }

        // Compras
        composable("C_01_CarritoCompra") { C_01_CarritoCompra(navController) }
        composable("C_02_ResumenCompra") { C_02_ResumenCarritoCompra(navController) }
        composable("C_04_CompraExitosa") { C_04_CompraExitosa(navController) }
        composable("C_05_DetallesCompra") { C_05_BoletaCompra(navController) }

        // Ventas
        composable("V_01_CarritoVenta") { V_01_CarritoVenta(navController) }
        composable("V_02_ResumenVenta") { V_02_ResumenCarritoVenta(navController) }
        composable("V_04_VentaExitosa") { V_04_VentaExitosa(navController) }
        composable("V_05_DetallesVenta") { V_05_BoletaVenta(navController) }
    }
}
