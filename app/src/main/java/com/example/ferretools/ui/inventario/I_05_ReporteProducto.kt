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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ferretools.ui.components.SelectorOpciones
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.reporte.ResumenBox
import com.example.ferretools.ui.components.seleccion_productos.DropdownBar
import com.example.ferretools.viewmodel.inventario.ReporteProductoViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.stacked
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
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
                .verticalScroll(rememberScrollState())
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
                seleccionado = reporteProductoUiState.value.operacionSeleccionada
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
                            DropdownBar(
                                opciones = listOf("Diario", "Semanal", "Mensual"),
                                opcionPorDefecto = "Diario",
                                onOpcionSeleccionada = { reporteProductoViewModel.cambiarPeriodoTemporal(it, productoId) }
                            )
                            DropdownBar(
                                opciones = listOf("Barras", "Apiladas"),
                                opcionPorDefecto = "Barras",
                                onOpcionSeleccionada = { reporteProductoViewModel.cambiarTipoGrafico(it, productoId) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (reporteProductoUiState.value.operacionSeleccionada == "Ventas") {
                        if (reporteProductoUiState.value.ventas != null) {
                            if (reporteProductoUiState.value.ventas!!.isNotEmpty()) {

                                Log.e("DEBUG", "En ventas: ${reporteProductoUiState.value.valoresGrafico}")
                                Log.e("DEBUG", "En ventas: ${reporteProductoUiState.value.fechasGrafico}")

                                when (reporteProductoUiState.value.tipoGrafico) {
                                    "Barras" -> {
                                        if (reporteProductoViewModel.hayDatos(reporteProductoUiState.value.valoresGrafico)
                                            && reporteProductoUiState.value.fechasGrafico.isNotEmpty()) {
                                            GraficoBarrasPorPeriodo(
                                                datos = reporteProductoUiState.value.valoresGrafico,
                                                fechas = reporteProductoUiState.value.fechasGrafico,
                                                periodoTemporal = reporteProductoUiState.value.periodoTemporal
                                            )
                                        } else {
                                            NoDataFoundCard()
                                        }
                                    }
                                    "Apiladas" -> {
                                        if (reporteProductoUiState.value.datosGraficoPorUsuario.isNotEmpty()
                                            && reporteProductoUiState.value.fechasGrafico.isNotEmpty()) {
                                            GraficoBarrasApiladasPorUsuario(
                                                datosPorUsuario = reporteProductoUiState.value.datosGraficoPorUsuario,
                                                fechas = reporteProductoUiState.value.fechasGrafico,
                                                periodoTemporal = reporteProductoUiState.value.periodoTemporal
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
                                NoItemsFoundCard(operacion = reporteProductoUiState.value.operacionSeleccionada)
                            }
                        }
                    } else {
                        if (reporteProductoUiState.value.compras != null) {
                            if (reporteProductoUiState.value.compras!!.isNotEmpty()) {

                                Log.e("DEBUG", "En compras: ${reporteProductoUiState.value.valoresGrafico}")
                                Log.e("DEBUG", "EN compras: ${reporteProductoUiState.value.fechasGrafico}")

                                when (reporteProductoUiState.value.tipoGrafico) {
                                    "Barras" -> {
                                        if (reporteProductoViewModel.hayDatos(reporteProductoUiState.value.valoresGrafico)
                                            && reporteProductoUiState.value.fechasGrafico.isNotEmpty()) {
                                            GraficoBarrasPorPeriodo(
                                                datos = reporteProductoUiState.value.valoresGrafico,
                                                fechas = reporteProductoUiState.value.fechasGrafico,
                                                periodoTemporal = reporteProductoUiState.value.periodoTemporal
                                            )
                                        } else {
                                            NoDataFoundCard()
                                        }
                                    }
                                    "Apiladas" -> {
                                        Log.d("DEBUG", "Cambiar a barras apiladas")
                                        if (reporteProductoUiState.value.datosGraficoPorUsuario.isNotEmpty()
                                            && reporteProductoUiState.value.fechasGrafico.isNotEmpty()) {
                                            GraficoBarrasApiladasPorUsuario(
                                                datosPorUsuario = reporteProductoUiState.value.datosGraficoPorUsuario,
                                                fechas = reporteProductoUiState.value.fechasGrafico,
                                                periodoTemporal = reporteProductoUiState.value.periodoTemporal
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
                            etiqueta = "unidades\nvendidas"
                        )
                        ResumenBox(
                            titulo = "Total\nrecaudado",
                            valor = reporteProductoUiState.value.totalRecaudadoVentas.toString(),
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
                            valor = reporteProductoUiState.value.gananciaPromedioVenta.toString(),
                            etiqueta = "soles recaudados\npor cada unidad"
                        )
                        ResumenBox(
                            titulo = "Usuario con mayores ventas",
                            valor = reporteProductoUiState.value.usuarioMayoresVentas,
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
                            valor = reporteProductoUiState.value.unidadesCompradas.toString(),
                            etiqueta = "unidades\ncompradas"
                        )
                        ResumenBox(
                            titulo = "Total\nrecaudado",
                            valor = reporteProductoUiState.value.totalInvertidoCompras.toString(),
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
                            valor = reporteProductoUiState.value.precioPromedioCompra.toString(),
                            etiqueta = "soles invertidos\npor cada unidad"
                        )
                        ResumenBox(
                            titulo = "Usuario con mayores compras",
                            valor = reporteProductoUiState.value.usuarioMayoresCompras,
                            etiqueta = null
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
fun NoDataFoundCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Text("No hay datos que mostrar para ese producto")
    }
}

@Composable
fun GraficoBarrasPorPeriodo(
    datos: List<Float>,
    fechas: List<LocalDate>,
    periodoTemporal: String
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val xToFechasMapKey = remember { ExtraStore.Key<Map<Float, LocalDate>>() }

    val mapaFechasToDatos: Map<LocalDate, Float> = fechas.zip(datos).toMap()

    // Crea una nueva llave para cada elemento y lo asocia con el elemento original
    val xToFechas = fechas.indices.associate { it.toFloat() to fechas[it] }

    LaunchedEffect(fechas, datos) {
        modelProducer.runTransaction {
            columnSeries { series(xToFechas.keys, mapaFechasToDatos.values) }
            extras { extras -> extras[xToFechasMapKey] = xToFechas }
        }
    }
    var dateFormatter: DateTimeFormatter
    if (periodoTemporal == "Mensual") {
        dateFormatter = DateTimeFormatter.ofPattern("MMMM", Locale("es"))
    } else {
        dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale("es"))
    }

    val bottomFormatter = CartesianValueFormatter { context, x, _ ->
        val extrasXToFechas = context.model.extraStore.getOrNull(xToFechasMapKey)
        val fecha = extrasXToFechas?.get(x.toFloat()) ?: LocalDate.ofEpochDay(x.toLong())
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

@Composable
fun GraficoBarrasApiladasPorUsuario(
    datosPorUsuario: Map<String, List<Float>>,
    fechas: List<LocalDate>,
    periodoTemporal: String
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val xToFechasMapKey = remember { ExtraStore.Key<Map<Float, LocalDate>>() }
    val legendKey = remember { ExtraStore.Key<Set<String>>() }

    val x = fechas.indices.map { it.toFloat() }
    val xToFechas = x.zip(fechas).toMap()

    val colores = listOf(
        Color(0xFF1E88E5), Color(0xFFD81B60), Color(0xFF43A047), Color(0xFFF4511E),
        Color(0xFF6A1B9A), Color(0xFF00897B), Color(0xFF5D4037), Color(0xFF3949AB)
    )

    Log.d("DEBUG", "Usuarios: ${datosPorUsuario.keys}")
    Log.d("DEBUG", "Fechas: ${fechas}")


    LaunchedEffect(datosPorUsuario, fechas) {
        modelProducer.runTransaction {
            columnSeries {
                datosPorUsuario.values.forEach { lista ->
                    if (lista.isNotEmpty()) {
                        series(x, lista)
                    }
                }
            }
            extras {
                it[xToFechasMapKey] = xToFechas
                it[legendKey] = datosPorUsuario.keys
            }
        }
    }


    val dateFormatter = when (periodoTemporal) {
        "Mensual" -> DateTimeFormatter.ofPattern("MMM", Locale("es"))
        else -> DateTimeFormatter.ofPattern("dd MMM", Locale("es"))
    }

    val bottomFormatter = CartesianValueFormatter { context, xVal, _ ->
        val fecha = context.model.extraStore.getOrNull(xToFechasMapKey)?.get(xVal.toFloat())
        fecha?.format(dateFormatter) ?: ""
    }

    val labelComponent = rememberTextComponent(vicoTheme.textColor)

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider =
                    ColumnCartesianLayer.ColumnProvider.series(
                        colores.map { color ->
                            rememberLineComponent(fill = fill(color), thickness = 16.dp)
                        }
                    ),
                mergeMode = { ColumnCartesianLayer.MergeMode.stacked() },
                columnCollectionSpacing = 24.dp,
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = bottomFormatter),
            legend = rememberHorizontalLegend(
                items = { extraStore ->
                    extraStore[legendKey].forEachIndexed { index, label ->
                        add(
                            LegendItem(
                                shapeComponent(
                                    fill(colores[index % colores.size]),
                                    CorneredShape.Pill
                                ),
                                labelComponent,
                                label
                            )
                        )
                    }
                },
                padding = insets(top = 12.dp)
            )
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    )
}
