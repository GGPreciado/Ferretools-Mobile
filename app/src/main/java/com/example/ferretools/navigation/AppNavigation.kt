package com.example.ferretools.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.navigation.navArgument
import com.example.ferretools.ui.balance.B_01_Balances
import com.example.ferretools.ui.balance.B_02_Detalles
import com.example.ferretools.ui.balance.B_03_Reporte
import com.example.ferretools.ui.compra.C_01_CarritoCompra
import com.example.ferretools.ui.compra.C_02_ResumenCarritoCompra
import com.example.ferretools.ui.compra.C_04_CompraExitosa
import com.example.ferretools.ui.compra.C_05_BoletaCompra
import com.example.ferretools.ui.configuracion.Config_01_Configuracion
import com.example.ferretools.ui.configuracion.Config_02_EditarPerfil
import com.example.ferretools.ui.configuracion.Config_03_EditarNegocio
import com.example.ferretools.ui.configuracion.Config_04_CambiarQRYape
import com.example.ferretools.ui.configuracion.Config_05_CambiarContrasena
import com.example.ferretools.ui.home.HOME_Admin
import com.example.ferretools.ui.home.HOME_Cliente
import com.example.ferretools.ui.home.HOME_Empleado
import com.example.ferretools.ui.home.PedidoCliente
import com.example.ferretools.ui.inventario.I_01_ListaProductos
import com.example.ferretools.ui.inventario.I_02_AgregarProducto
import com.example.ferretools.ui.inventario.I_04_DetallesProducto
import com.example.ferretools.ui.inventario.I_05_ReporteProducto
import com.example.ferretools.ui.inventario.I_06_EditarProducto
import com.example.ferretools.ui.inventario.I_08_ListaCategorias
import com.example.ferretools.ui.inventario.I_09_CrearCategoria
import com.example.ferretools.ui.inventario.I_10_DetallesCategoria
import com.example.ferretools.ui.inventario.I_12_ReporteInventario
import com.example.ferretools.viewmodel.inventario.AgregarProductoViewModel
import com.example.ferretools.viewmodel.inventario.ListaCategoriasViewModel
import com.example.ferretools.viewmodel.inventario.DetallesProductoViewModel
import com.example.ferretools.viewmodel.inventario.ReporteInventarioViewModel
import com.example.ferretools.viewmodel.inventario.ListaProductosViewModel
import com.example.ferretools.ui.pedido.P_01_AgregarAlCarrito
import com.example.ferretools.ui.pedido.P_02_CarritoCliente
import com.example.ferretools.ui.pedido.P_03_ConfirmarPedido
import com.example.ferretools.ui.pedido.P_04_PedidoExitoso
import com.example.ferretools.ui.pedido.P_05_HistorialPedidos
import com.example.ferretools.ui.pedido.P_06_BoletaPedido
import com.example.ferretools.ui.pedido.P_E1_HistorialPedidos
import com.example.ferretools.ui.pedido.P_E2_DetallesPedido
import com.example.ferretools.ui.pedido.P_E3_PrepararPedido
import com.example.ferretools.ui.pedido.PedidoDetalle
import com.example.ferretools.ui.pedido.PedidoHistorial
import com.example.ferretools.ui.session.S_01_PortadaBienvenida
import com.example.ferretools.ui.session.S_02_SeleccionRol
import com.example.ferretools.ui.session.S_03_02_ElegirNegocio
import com.example.ferretools.ui.session.S_03_RegistroUsuario
import com.example.ferretools.ui.session.S_04_RegistroNegocio
import com.example.ferretools.ui.session.S_05_IniciarSesion
import com.example.ferretools.ui.session.S_06_RecuperarContrasena
import com.example.ferretools.ui.session.S_07_CambiarContrasena
import com.example.ferretools.ui.venta.V_01_CarritoVenta
import com.example.ferretools.ui.venta.V_02_ResumenCarritoVenta
import com.example.ferretools.ui.venta.V_04_VentaExitosa
import com.example.ferretools.ui.venta.V_05_BoletaVenta
import com.example.ferretools.viewmodel.inventario.EditarProductoViewModel
import com.example.ferretools.viewmodel.compra.CompraViewModel
import androidx.navigation.NavType
import com.example.ferretools.ui.venta.BarcodeScannerScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    //val navController = rememberNavController()

    // --- MOCKS Y DATOS DE EJEMPLO ---
    val mockPedidosHistorial = listOf(
        PedidoHistorial(
            id = "001",
            cliente = "Juan Pérez",
            fecha = "2024-06-10",
            productos = listOf("Arroz x2", "Azúcar x1"),
            estado = "Entregado"
        )
    )
    val mockPedidoDetalle = PedidoDetalle(
        id = "001",
        cliente = "Juan Pérez",
        fecha = "2024-06-10",
        productos = listOf("Arroz x2", "Azúcar x1"),
        estado = "Pendiente"
    )
    val pedidosDemo = listOf(
        PedidoCliente("1001", "2024-06-10", "Entregado", "S/ 45.00"),
        PedidoCliente("1002", "2024-06-12", "Listo", "S/ 30.00"),
        PedidoCliente("1003", "2024-06-13", "En preparación", "S/ 20.00")
    )

    // ViewModel compartido para el flujo de compras
    val compraViewModel: CompraViewModel = viewModel()
    
    // ViewModel compartido para el flujo de ventas
    val ventaViewModel: com.example.ferretools.viewmodel.venta.VentaViewModel = viewModel()

    // ViewModel compartido para el flujo de pedidos
    val pedidoViewModel: com.example.ferretools.viewmodel.pedido.PedidoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Auth.WELCOME
    ) {
        // Auth Stack
        composable(AppRoutes.Auth.WELCOME) {
            S_01_PortadaBienvenida(navController = navController)
        }
        composable(AppRoutes.Auth.SELECT_ROLE) {
            S_02_SeleccionRol(navController = navController)
        }
        // Ruta type-safe con parámetro
        composable<AppRoutes.Auth.REGISTER_USER> { backStackEntry ->
            val registerUser: AppRoutes.Auth.REGISTER_USER = backStackEntry.toRoute()
            S_03_RegistroUsuario(
                navController = navController,
                rolUsuario = registerUser.rolUsuario
            )
        }
        composable<AppRoutes.Auth.SELECT_BUSINESS> { backStackEntry ->
            val selectBusiness: AppRoutes.Auth.SELECT_BUSINESS = backStackEntry.toRoute()
            S_03_02_ElegirNegocio(
                navController = navController,
                rolUsuario = selectBusiness.rolUsuario
            )
        }
        composable(AppRoutes.Auth.REGISTER_BUSINESS) {
            S_04_RegistroNegocio(navController = navController)

        }
        composable(AppRoutes.Auth.LOGIN) {
            S_05_IniciarSesion(navController = navController)
        }
        composable(AppRoutes.Auth.RECOVER_PASSWORD) {
            S_06_RecuperarContrasena(navController = navController)
        }
        composable(AppRoutes.Auth.CHANGE_PASSWORD) {
            S_07_CambiarContrasena(navController = navController)
        }

        // Configuración Stack
        composable(AppRoutes.Config.MAIN) {
            Config_01_Configuracion(
                navController = navController,
                darkModeEnabled = false,
                stockNotificationEnabled = false
            )
        }
        composable(AppRoutes.Config.EDIT_PROFILE) {
            Config_02_EditarPerfil(
                navController = navController
            )
        }
        composable(AppRoutes.Config.EDIT_BUSINESS) {
            Config_03_EditarNegocio(
                navController = navController
            )
        }
        composable(AppRoutes.Config.CHANGE_QR) {
            Config_04_CambiarQRYape(navController = navController)
        }
        composable(AppRoutes.Config.CHANGE_PASSWORD) {
            Config_05_CambiarContrasena(navController = navController)
        }
        composable(AppRoutes.Config.SOLICITUDES) {
            com.example.ferretools.ui.configuracion.SolicitudesScreen(navController = navController)
        }
        composable(AppRoutes.Config.MI_SOLICITUD) {
            com.example.ferretools.ui.configuracion.MiSolicitudScreen(navController = navController)
        }
        // Dashboard Stack
        composable(AppRoutes.Admin.DASHBOARD) {
            HOME_Admin(navController = navController)
        }
        composable(AppRoutes.Client.DASHBOARD) {
            HOME_Cliente(navController = navController)
        }
        composable(AppRoutes.Employee.DASHBOARD) {
            HOME_Empleado(navController = navController)
        }
        // Inventario Stack
        composable(AppRoutes.Inventory.LIST_PRODUCTS) {
            I_01_ListaProductos(navController = navController)
        }
        composable(AppRoutes.Inventory.ADD_PRODUCT) {
            val viewModel: AgregarProductoViewModel = viewModel()
            I_02_AgregarProducto(
                navController = navController,
                viewModel = viewModel
            )
        }
        // Escáner de código de barras para inventario
        composable(AppRoutes.Inventory.BARCODE_SCANNER) {
            BarcodeScannerScreen(
                navController = navController,
                onBarcodeScanned = { barcode ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("barcode_result", barcode)
                    android.util.Log.d("AppNavigation", "Código de barras escaneado en inventario: $barcode")
                    // Eliminar navegación automática, el usuario debe volver manualmente
                }
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

        composable<AppRoutes.Inventory.PRODUCT_REPORT> { backStackEntry ->
            val productReport: AppRoutes.Inventory.PRODUCT_REPORT = backStackEntry.toRoute()
            I_05_ReporteProducto(
                navController = navController,
                productoId = productReport.productoId,
                productoNombre = productReport.productoNombre
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
        // Compras Stack
        composable(AppRoutes.Purchase.CART) {
            C_01_CarritoCompra(navController = navController, compraViewModel = compraViewModel)
        }
        // Escáner de código de barras para compras
        composable(AppRoutes.Purchase.BARCODE_SCANNER) {
            BarcodeScannerScreen(
                navController = navController,
                onBarcodeScanned = { barcode ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("barcode_result", barcode)
                    android.util.Log.d("AppNavigation", "Código de barras escaneado en compras: $barcode")
                    //navController.popBackStack()
                }
            )
        }
        composable(AppRoutes.Purchase.CART_SUMMARY) {
            C_02_ResumenCarritoCompra(navController = navController, viewModel = compraViewModel)
        }
        composable(AppRoutes.Purchase.SUCCESS) {
            C_04_CompraExitosa(navController = navController, viewModel = compraViewModel)
        }
        composable(AppRoutes.Purchase.RECEIPT) {
            C_05_BoletaCompra(navController = navController, viewModel = compraViewModel)
        }
        // Ventas Stack
        composable(AppRoutes.Sale.CART) {
            V_01_CarritoVenta(navController = navController, viewModel = ventaViewModel)
        }
        composable(AppRoutes.Sale.CART_SUMMARY) {
            V_02_ResumenCarritoVenta(navController = navController, viewModel = ventaViewModel)
        }
        composable(AppRoutes.Sale.SUCCESS) {
            V_04_VentaExitosa(navController = navController)
        }
        composable(AppRoutes.Sale.RECEIPT) {
            V_05_BoletaVenta(navController = navController, viewModel = ventaViewModel)
        }
        composable(AppRoutes.Sale.BARCODE_SCANNER) {
            BarcodeScannerScreen(
                navController = navController,
                onBarcodeScanned = { barcode ->
                    // Guardar el código escaneado en el SavedStateHandle para que lo lea V_01_CarritoVenta
                    navController.previousBackStackEntry?.savedStateHandle?.set("scanned_barcode", barcode)
                    android.util.Log.d("AppNavigation", "Código de barras escaneado en ventas: $barcode")
                    //navController.popBackStack()
                }
            )
        }
        // Pedidos Stack
        composable(AppRoutes.Order.ADD_TO_CART) {
            P_01_AgregarAlCarrito(navController = navController, viewModel = pedidoViewModel)
        }
        // Escáner de código de barras para pedidos
        composable(AppRoutes.Order.BARCODE_SCANNER) {
            BarcodeScannerScreen(
                navController = navController,
                onBarcodeScanned = { barcode ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("barcode_result", barcode)
                    android.util.Log.d("AppNavigation", "Código de barras escaneado en pedidos: $barcode")
                    //navController.popBackStack()
                }
            )
        }
        composable(AppRoutes.Order.CART) {
            P_02_CarritoCliente(navController = navController, viewModel = pedidoViewModel)
        }
        composable(AppRoutes.Order.CONFIRM) {
            P_03_ConfirmarPedido(navController = navController)
        }
        composable(AppRoutes.Order.SUCCESS) {
            P_04_PedidoExitoso(navController = navController)
        }
        composable(AppRoutes.Order.HISTORY) {
            P_05_HistorialPedidos(
                navController = navController,
                pedidosHistorial = pedidosDemo,
                selectedMenu = 2,
                onMenuSelect = {},
                onPedidoClick = {},
                userName = "Cliente Demo",
                storeName = "Tienda Demo"
            )
        }
        composable(AppRoutes.Order.RECEIPT) {
            P_06_BoletaPedido(navController = navController, viewModel = pedidoViewModel)
        }
        composable(AppRoutes.Order.Employee.HISTORY) {
            P_E1_HistorialPedidos(
                navController = navController,
                pedidosHistorial = mockPedidosHistorial,
                onPedidoClick = {},
                userName = "Empleado Demo",
                storeName = "Tienda Demo"
            )
        }
        composable(
            route = AppRoutes.Order.DETAILS,
            arguments = listOf(navArgument("pedidoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getString("pedidoId") ?: ""
            P_E2_DetallesPedido(navController = navController, pedidoId = pedidoId)
        }
        composable(
            route = AppRoutes.Order.Employee.PREPARE,
            arguments = listOf(navArgument("pedidoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getString("pedidoId") ?: ""
            P_E3_PrepararPedido(
                navController = navController,
                pedidoId = pedidoId,
                onPedidoPreparado = {}
            )
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
    }
}