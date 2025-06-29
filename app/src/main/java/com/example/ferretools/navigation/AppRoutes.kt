package com.example.ferretools.navigation

import com.example.ferretools.model.enums.RolUsuario
import kotlinx.serialization.Serializable

/**
 * Objeto que contiene todas las rutas de navegación de la aplicación.
 * Las rutas están organizadas por secciones para mejor mantenimiento.
 */
object AppRoutes {
    // Rutas de Autenticación
    object Auth {
        const val WELCOME = "welcome"
        const val SELECT_ROLE = "select_role"
        //const val SELECT_BUSINESS = "select_business"
        const val REGISTER_BUSINESS = "register_business"
        const val LOGIN = "login"
        const val RECOVER_PASSWORD = "recover_password"
        const val CHANGE_PASSWORD = "change_password"

        @Serializable
        data class REGISTER_USER(val rolUsuario: RolUsuario)
        @Serializable
        data class SELECT_BUSINESS(val rolUsuario: RolUsuario)
    }

    // Rutas de Admin
    object Admin {
        const val DASHBOARD = "admin_dashboard"
        const val INVENTORY = "admin_inventory"
        const val PURCHASES = "admin_purchases"
        const val SALES = "admin_sales"
        const val BALANCE = "admin_balance"
        const val CONFIG = "admin_config"
    }

    // Rutas de Cliente
    object Client {
        const val DASHBOARD = "client_dashboard"
        const val CATALOG = "client_catalog"
        const val ORDERS = "client_orders"
        const val CONFIG = "client_config"
    }

    // Rutas de Almacenero
    object Employee {
        const val DASHBOARD = "employee_dashboard"
        const val ORDERS = "employee_orders"
        const val INVENTORY = "employee_inventory"
        const val PURCHASES = "employee_purchases"
        const val SALES = "employee_sales"
        const val CONFIG = "employee_config"
    }

    // Rutas de Inventario
    object Inventory {
        const val LIST_PRODUCTS = "inventory_list"
        const val ADD_PRODUCT = "inventory_add"
        const val PRODUCT_ADDED = "inventory_product_added"
        const val PRODUCT_DETAILS = "inventory_product_details"
//        const val PRODUCT_REPORT = "inventory_product_report"
        const val EDIT_PRODUCT = "inventory_edit"
        const val DELETE_PRODUCT = "inventory_delete"
        const val LIST_CATEGORIES = "inventory_categories"
        const val ADD_CATEGORY = "inventory_add_category"
        const val CATEGORY_DETAILS = "inventory_category_details/{categoriaId}"
        const val CATEGORY_ADDED = "inventory_category_added"
        const val INVENTORY_REPORT = "inventory_report"
        const val SHARE_REPORT = "inventory_share_report"

        @Serializable
        data class PRODUCT_REPORT(val productoId: String, val productoNombre: String)
    }

    // Rutas de Compras
    object Purchase {
        const val CART = "purchase_cart"
        const val CART_SUMMARY = "purchase_cart_summary"
        const val CONFIRM = "purchase_confirm"
        const val SUCCESS = "purchase_success"
        const val RECEIPT = "purchase_receipt"
    }

    // Rutas de Ventas
    object Sale {
        const val CART = "sale_cart"
        const val CART_SUMMARY = "sale_cart_summary"
        const val CONFIRM = "sale_confirm"
        const val SUCCESS = "sale_success"
        const val RECEIPT = "sale_receipt"
    }

    // Rutas de Pedidos
    object Order {
        const val ADD_TO_CART = "order_add_to_cart"
        const val CART = "order_cart"
        const val CONFIRM = "order_confirm"
        const val SUCCESS = "order_success"
        const val HISTORY = "order_history"
        const val RECEIPT = "order_receipt"

        // Rutas específicas para empleados
        object Employee {
            const val HISTORY = "order_employee_history"
            const val DETAILS = "order_employee_details"
            const val PREPARE = "order_employee_prepare"
        }
    }

    // Rutas de Balance
    object Balance {
        const val LIST = "balance_list"
        const val DETAILS = "balance_details"
        const val REPORT = "balance_report"
    }

    // Rutas de Configuración
    object Config {
        const val MAIN = "config_main"
        const val EDIT_PROFILE = "config_edit_profile"
        const val EDIT_BUSINESS = "config_edit_business"
        const val CHANGE_QR = "config_change_qr"
        const val CHANGE_PASSWORD = "config_change_password"
        const val CONFIRM_LOGOUT = "config_confirm_logout"
        const val SOLICITUDES = "config_solicitudes"
        const val MI_SOLICITUD = "config_mi_solicitud"
    }

    // Función helper para construir rutas con parámetros
    fun buildRoute(baseRoute: String, vararg params: Pair<String, String>): String {
        return if (params.isEmpty()) {
            baseRoute
        } else {
            val queryString = params.joinToString("&") { (key, value) ->
                "$key=$value"
            }
            "$baseRoute?$queryString"
        }
    }

    // Función helper para extraer parámetros de una ruta
    fun extractParams(route: String): Map<String, String> {
        return if (!route.contains("?")) {
            emptyMap()
        } else {
            val queryString = route.substringAfter("?")
            queryString.split("&").associate { param ->
                val (key, value) = param.split("=")
                key to value
            }
        }
    }
}