package com.example.ferretools.ui.inventario

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ferretools.ui.components.SelectorOpciones
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.reporte.ResumenBox
import com.example.ferretools.viewmodel.inventario.ReporteProductoViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun I_05_ReporteProducto(
    navController: NavController,
    productoId: String,
    productoNombre: String,
    reporteProductoViewModel: ReporteProductoViewModel = viewModel()
) {
    val reporteProductoUiState = reporteProductoViewModel.uiState.collectAsState()

    LaunchedEffect(productoId) {
        reporteProductoViewModel.cargarVentasDeProducto(productoId)
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
                Text("Reporte de ${reporteProductoUiState.value.operacionSeleccionada.lowercase()} del producto $productoNombre", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de ventas/compras
            SelectorOpciones(
                opcion1 = "Ventas",
                opcion2 = "Compras",
                seleccionado = ""
            ) {
                reporteProductoViewModel.cambiarOperacionSeleccionada(it)
                if (it == "Ventas") {
                    reporteProductoViewModel.cargarVentasDeProducto(productoId)
                } else {
                    reporteProductoViewModel.cargarComprasDeProducto(productoId)
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
                            "Estadísticas de ${reporteProductoUiState.value.operacionSeleccionada}",
                            fontWeight = FontWeight.Bold
                        )
                        // Selector de periodo
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Monthly", fontSize = 13.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (reporteProductoUiState.value.operacionSeleccionada == "Ventas") {
                        if (reporteProductoUiState.value.ventas != null) {
                            if (reporteProductoUiState.value.ventas!!.isNotEmpty()) {

                                Log.e("DEBUG", "En ventas: ${reporteProductoUiState.value.valoresGrafico}")
                                Log.e("DEBUG", "En ventas: ${reporteProductoUiState.value.fechasGrafico}")

                                GraficoVentasPorPeriodo(
                                    datos = reporteProductoUiState.value.valoresGrafico,
                                    fechas = reporteProductoUiState.value.fechasGrafico,
                                )

                            } else {
                                NoItemsFoundCard(operacion = reporteProductoUiState.value.operacionSeleccionada)
                            }
                        }
                    } else {
                        if (reporteProductoUiState.value.compras != null) {
                            if (reporteProductoUiState.value.compras!!.isNotEmpty()) {

                                Log.e("DEBUG", "En compras: ${reporteProductoUiState.value.valoresGrafico}")
                                Log.e("DEBUG", "EN compras: ${reporteProductoUiState.value.fechasGrafico}")

                                GraficoVentasPorPeriodo(
                                    datos = reporteProductoUiState.value.valoresGrafico,
                                    fechas = reporteProductoUiState.value.fechasGrafico,
                                )

                            } else {
                                NoItemsFoundCard(operacion = reporteProductoUiState.value.operacionSeleccionada)
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
                if (reporteProductoUiState.value.operacionSeleccionada == "Ventas") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        ResumenBox(
                            titulo = "Unidades\nvendidas",
                            valor = reporteProductoUiState.value.unidadesVendidas.toString(),
                            etiqueta = "+15%"
                        )
                        ResumenBox(
                            titulo = "Total\nrecaudado",
                            valor = reporteProductoUiState.value.totalRecaudadoVentas.toString(),
                            etiqueta = "+15%"
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ResumenBox(
                            titulo = "Ganancia promedio\npor venta",
                            valor = reporteProductoUiState.value.gananciaPromedioVenta.toString(),
                            etiqueta = "+15%"
                        )
                        ResumenBox(
                            titulo = "Usuario con mayores ventas",
                            valor = reporteProductoUiState.value.usuarioMayoresVentas,
                            etiqueta = "+15%"
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        ResumenBox(
                            titulo = "Unidades\ncompradas",
                            valor = reporteProductoUiState.value.unidadesCompradas.toString(),
                            etiqueta = "+15%"
                        )
                        ResumenBox(
                            titulo = "Total\nrecaudado",
                            valor = reporteProductoUiState.value.totalInvertidoCompras.toString(),
                            etiqueta = "+15%"
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ResumenBox(
                            titulo = "Precio promedio\npor compra",
                            valor = reporteProductoUiState.value.precioPromedioCompra.toString(),
                            etiqueta = "+15%"
                        )
                        ResumenBox(
                            titulo = "Usuario con mayores compras",
                            valor = reporteProductoUiState.value.usuarioMayoresCompras,
                            etiqueta = "+15%"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoItemsFoundCard(
    operacion: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Text("No se encontró ${operacion.lowercase()} para ese producto")
    }
}

@Composable
fun GraficoVentasPorPeriodo(
    datos: List<Float>,
    fechas: List<LocalDate>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val xToDateMapKey = remember { ExtraStore.Key<Map<Float, LocalDate>>() }

    val mapa: Map<LocalDate, Float> = fechas.zip(datos).toMap()
    val xToDates = mapa.keys.associateBy { it.toEpochDay().toFloat() }

    LaunchedEffect(fechas, datos) {
        modelProducer.runTransaction {
            columnSeries { series(xToDates.keys, mapa.values) }
            extras { extras -> extras[xToDateMapKey] = xToDates }
        }
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM")
    val bottomFormatter = CartesianValueFormatter { context, x, _ ->
        val map = context.model.extraStore.getOrNull(xToDateMapKey)
        val fecha = map?.get(x.toFloat()) ?: LocalDate.ofEpochDay(x.toLong())
        fecha.format(dateFormatter)
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomFormatter
            )
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    )
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun I_05_ReporteProductoPreview() {
//    val navController = rememberNavController()
//    I_05_ReporteProducto(navController = navController)
//}

//                    Botón PDF
//                    Button(
//                        onClick = { /* Acción PDF */ },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(44.dp),
//                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
//                    ) {
//                        Text("Convertir a PDF", color = Color.Black)
//                    }