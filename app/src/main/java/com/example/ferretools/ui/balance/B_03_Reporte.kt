package com.example.ferretools.ui.balance

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.ui.components.SelectorOpciones
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.reporte.ResumenBox
import com.example.ferretools.ui.components.seleccion_productos.DropdownBar
import com.example.ferretools.ui.inventario.GraficoBarrasApiladasPorUsuario
import com.example.ferretools.ui.inventario.GraficoBarrasPorPeriodo
import com.example.ferretools.ui.inventario.NoDataFoundCard
import com.example.ferretools.ui.inventario.NoItemsFoundCard
import com.example.ferretools.viewmodel.balance.ReporteBalanceViewModel

@Composable
fun B_03_Reporte(
    navController: NavController,
    reporteBalanceViewModel: ReporteBalanceViewModel = viewModel()
) {
    val reporteBalanceUiState = reporteBalanceViewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        reporteBalanceViewModel.cargarVentasDeProducto()
    }

    Scaffold(
        topBar = { TopNavBar(navController, "Reporte por Producto") }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp)) // Espacio adicional arriba de los elementos

            // Botón de fecha
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                onClick = {},
            ) {
                Text("Reporte de ${reporteBalanceUiState.operacionSeleccionada.lowercase()}", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de ventas/compras
            SelectorOpciones(
                opcion1 = "Ventas",
                opcion2 = "Compras",
                seleccionado = reporteBalanceUiState.operacionSeleccionada
            ) {
                reporteBalanceViewModel.cambiarOperacionSeleccionada(it)
                if (it == "Ventas") {
                    reporteBalanceViewModel.cargarVentasDeProducto()
                } else {
                    reporteBalanceViewModel.cargarComprasDeProducto()
                }
            }

            Spacer(Modifier.padding(vertical = 8.dp))

            // Box del gráfico
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(300.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Estadísticas de ${reporteBalanceUiState.operacionSeleccionada}",
                            fontWeight = FontWeight.Bold
                        )
                        // Selector de periodo
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DropdownBar(
                                opciones = listOf("Diario", "Semanal", "Mensual"),
                                opcionPorDefecto = "Diario",
                                onOpcionSeleccionada = { reporteBalanceViewModel.cambiarPeriodoTemporal(it) }
                            )
                            DropdownBar(
                                opciones = listOf("Barras", "Circular", "Apiladas"),
                                opcionPorDefecto = "Barras",
                                onOpcionSeleccionada = { reporteBalanceViewModel.cambiarTipoGrafico(it) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (reporteBalanceUiState.operacionSeleccionada == "Ventas") {
                        if (reporteBalanceUiState.listaVentas != null) {
                            if (reporteBalanceUiState.listaVentas.isNotEmpty()) {

                                Log.e("DEBUG", "En ventas: ${reporteBalanceUiState.datosGrafico}")
                                Log.e("DEBUG", "En ventas: ${reporteBalanceUiState.fechasGrafico}")

                                when (reporteBalanceUiState.tipoGrafico) {
                                    "Barras" -> {
                                        if (reporteBalanceViewModel.hayDatos(reporteBalanceUiState.datosGrafico)
                                            && reporteBalanceUiState.fechasGrafico.isNotEmpty()) {
                                            GraficoBarrasPorPeriodo(
                                                datos = reporteBalanceUiState.datosGrafico,
                                                fechas = reporteBalanceUiState.fechasGrafico,
                                                periodoTemporal = reporteBalanceUiState.periodoTemporal
                                            )
                                        } else {
                                            NoDataFoundCard()
                                        }
                                    }
                                    "Apiladas" -> {
                                        if (reporteBalanceUiState.datosGraficoPorUsuario.isNotEmpty()
                                            && reporteBalanceUiState.fechasGrafico.isNotEmpty()) {
                                            GraficoBarrasApiladasPorUsuario(
                                                datosPorUsuario = reporteBalanceUiState.datosGraficoPorUsuario,
                                                fechas = reporteBalanceUiState.fechasGrafico,
                                                periodoTemporal = reporteBalanceUiState.periodoTemporal
                                            )
                                        } else {
                                            NoDataFoundCard()
                                        }

                                    }
                                    else -> {
                                        Log.d("DEBUG", "Elija un tipo de gráfico válido")
                                    }
                                }

                            } else {
                                NoItemsFoundCard(operacion = reporteBalanceUiState.operacionSeleccionada)
                            }
                        }
                    } else {
                        if (reporteBalanceUiState.listaCompras != null) {
                            if (reporteBalanceUiState.listaCompras.isNotEmpty()) {

                                Log.e("DEBUG", "En compras: ${reporteBalanceUiState.datosGrafico}")
                                Log.e("DEBUG", "EN compras: ${reporteBalanceUiState.fechasGrafico}")

                                when (reporteBalanceUiState.tipoGrafico) {
                                    "Barras" -> {
                                        if (reporteBalanceViewModel.hayDatos(reporteBalanceUiState.datosGrafico)
                                            && reporteBalanceUiState.fechasGrafico.isNotEmpty()) {
                                            GraficoBarrasPorPeriodo(
                                                datos = reporteBalanceUiState.datosGrafico,
                                                fechas = reporteBalanceUiState.fechasGrafico,
                                                periodoTemporal = reporteBalanceUiState.periodoTemporal
                                            )
                                        } else {
                                            NoDataFoundCard()
                                        }
                                    }
                                    "Apiladas" -> {
                                        Log.d("DEBUG", "Cambiar a barras apiladas")
                                        if (reporteBalanceUiState.datosGraficoPorUsuario.isNotEmpty()
                                            && reporteBalanceUiState.fechasGrafico.isNotEmpty()) {
                                            GraficoBarrasApiladasPorUsuario(
                                                datosPorUsuario = reporteBalanceUiState.datosGraficoPorUsuario,
                                                fechas = reporteBalanceUiState.fechasGrafico,
                                                periodoTemporal = reporteBalanceUiState.periodoTemporal
                                            )
                                        } else {
                                            NoDataFoundCard()
                                        }
                                    }
                                    else -> {
                                        Log.d("DEBUG", "Elija un tipo de gráfico válido")
                                    }
                                }


                            } else {
                                NoItemsFoundCard(operacion = reporteBalanceUiState.operacionSeleccionada)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cuadros de resumen
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
            ) {
                if (reporteBalanceUiState.operacionSeleccionada == "Ventas") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        ResumenBox(
                            titulo = "Unidades\nvendidas",
                            valor = "XXXX",
//                            valor = reporteBalanceUiState.unidadesVendidas.toString(),
                            etiqueta = "unidades\nvendidas"
                        )
                        ResumenBox(
                            titulo = "Total\nrecaudado",
                            valor = "XXXX",
//                            valor = reporteBalanceUiState.totalRecaudadoVentas.toString(),
                            etiqueta = "soles recaudados\npor ventas"
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ResumenBox(
                            titulo = "Ganancia promedio\npor venta",
                            valor = "XXXX",
//                            valor = reporteBalanceUiState.gananciaPromedioVenta.toString(),
                            etiqueta = "soles recaudados\npor cada unidad"
                        )
                        ResumenBox(
                            titulo = "Usuario con mayores ventas",
                            valor = "XXXX",
//                            valor = reporteBalanceUiState.usuarioMayoresVentas,
                            etiqueta = null
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        ResumenBox(
                            titulo = "Unidades\ncompradas",
                            valor = "XXXX",
//                            valor = reporteBalanceUiState.unidadesCompradas.toString(),
                            etiqueta = "unidades\ncompradas"
                        )
                        ResumenBox(
                            titulo = "Total\nrecaudado",
                            valor = "XXXX",
//                            valor = reporteBalanceUiState.totalInvertidoCompras.toString(),
                            etiqueta = "soles invertidos\nen compras"
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ResumenBox(
                            titulo = "Precio promedio\npor compra",
                            valor = "XXXX",
//                            valor = reporteBalanceUiState.precioPromedioCompra.toString(),
                            etiqueta = "soles invertidos\npor cada unidad"
                        )
                        ResumenBox(
                            titulo = "Usuario con mayores compras",
                            valor = "XXXX",
//                            valor = reporteBalanceUiState.usuarioMayoresCompras,
                            etiqueta = null
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewB_03_Reporte() {
    val navController = rememberNavController()
    B_03_Reporte(navController = navController)
}