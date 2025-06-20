package com.example.ferretools.ui.inventario

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
import androidx.compose.material3.CircularProgressIndicator
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
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import java.time.LocalDate

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
                onClick = { /* Acción seleccionar fecha */ },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
            ) {
                Text("Reporte de {compra/venta} del producto $productoNombre", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de ventas/compras
            SelectorOpciones(
                opcion1 = "Compras",
                opcion2 = "Ventas",
                seleccionado = ""
            ) { /* TODO: Función de selección de valor */ }

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
                        Text("Estadísticas de Venta", fontWeight = FontWeight.Bold)
                        // Selector de periodo
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Monthly", fontSize = 13.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (reporteProductoUiState.value.ventas != null) {
                        if (reporteProductoUiState.value.ventas!!.isNotEmpty()) {
                            val (valores, fechas) = reporteProductoViewModel.
                                agruparUnidadesPorPeriodo(
                                    productoId = productoId
                                )

                            GraficoVentasPorPeriodo(
                                datos = valores,
                                fechas = fechas,
                            )
                        } else {
                            CircularProgressIndicator()
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ResumenBox(
                        titulo = "Unidades\nvendidas",
                        valor = "XXXX",
                        porcentaje = "+15%"
                    )
                    ResumenBox(
                        titulo = "Total\nrecaudado",
                        valor = "XXXX",
                        porcentaje = "+15%"
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ResumenBox(
                        titulo = "Porcentaje dentro\nde categoría",
                        valor = "XXXX",
                        porcentaje = "+15%"
                    )
                    ResumenBox(
                        titulo = "Puesto dentro\nde categoría",
                        valor = "XXXX",
                        porcentaje = "+15%"
                    )
                }
            }
        }
    }
}

@Composable
fun GraficoVentasPorPeriodo(
    datos: List<Int>,
    fechas: List<LocalDate>
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val mapa: Map<LocalDate, Int> = fechas.zip(datos).toMap()

    val xToDateMapKey = ExtraStore.Key<Map<Float, LocalDate>>()
    val xToDates = mapa.keys.associateBy { it.toEpochDay().toFloat() }

    LaunchedEffect(fechas, datos) {
        modelProducer.runTransaction {
            columnSeries { series(xToDates.keys, mapa.values) }
            extras { it[xToDateMapKey] = xToDates }
        }
    }

//    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM")
//    val bottomFormatter = CartesianValueFormatter { context, x, _ ->
//        (context.model.extraStore[xToDateMapKey]?.get(x)
//            ?: LocalDate.ofEpochDay(x.toLong()))
//            .format(dateFormatter)
//    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
//                valueFormatter = bottomFormatter
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