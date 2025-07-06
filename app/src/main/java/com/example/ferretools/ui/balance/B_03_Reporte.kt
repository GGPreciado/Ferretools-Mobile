package com.example.ferretools.ui.balance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.R
import com.example.ferretools.ui.components.SelectorOpciones
import com.example.ferretools.ui.components.TopNavBar
import com.example.ferretools.ui.components.reporte.ResumenBox

private val YellowPrimary = Color(0xFFFFEB3B)
private val CardBorder = Color.Black

data class ResumenReporte(
    val productosDiferentes: Int,
    val categoriasDiferentes: Int,
    val productosTotales: Int,
    val gananciasTotales: Double,
    val productosDiferentesPorcentaje: String,
    val categoriasDiferentesPorcentaje: String,
    val productosTotalesPorcentaje: String,
    val gananciasTotalesPorcentaje: String
)

@Composable
fun B_03_Reporte(
    navController: NavController,
    // viewModel: ReporteViewModel = viewModel() // Para uso futuro
) {
    // Datos de ejemplo
    val resumen = ResumenReporte(
        productosDiferentes = 25,
        categoriasDiferentes = 8,
        productosTotales = 120,
        gananciasTotales = 3500.0,
        productosDiferentesPorcentaje = "+15%",
        categoriasDiferentesPorcentaje = "+10%",
        productosTotalesPorcentaje = "+20%",
        gananciasTotalesPorcentaje = "+18%"
    )
    var seleccionado by remember { mutableStateOf("Compras") }

    Scaffold(
        topBar = { TopNavBar(navController, "Reporte de Ventas y Compras") }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Selector de ventas/compras
            SelectorOpciones(
                opcion1 = "Compras",
                opcion2 = "Ventas",
                seleccionado = seleccionado
            ) { seleccionado = it }

            Spacer(Modifier.height(8.dp))

            // Box del gráfico
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(240.dp)
                    .border(2.dp, CardBorder, RoundedCornerShape(12.dp))
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
                            "Estadísticas de ${seleccionado}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        // Selector de periodo (puedes hacerlo interactivo si lo deseas)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Mensual", fontSize = 13.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Placeholder del gráfico
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.grafico),
                            contentDescription = "Gráfico",
                            modifier = Modifier.size(400.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón PDF
                    Button(
                        onClick = { /* TODO: Exportar a PDF */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
                    ) {
                        Text("Convertir a PDF", color = Color.Black)
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
                        titulo = "Productos\ndiferentes",
                        valor = resumen.productosDiferentes.toString(),
                        etiqueta = resumen.productosDiferentesPorcentaje
                    )
                    ResumenBox(
                        titulo = "Categorías\ndiferentes",
                        valor = resumen.categoriasDiferentes.toString(),
                        etiqueta = resumen.categoriasDiferentesPorcentaje
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ResumenBox(
                        titulo = "Productos\ntotales",
                        valor = resumen.productosTotales.toString(),
                        etiqueta = resumen.productosTotalesPorcentaje
                    )
                    ResumenBox(
                        titulo = "Ganancias\ntotales",
                        valor = "S/ ${resumen.gananciasTotales}",
                        etiqueta = resumen.gananciasTotalesPorcentaje
                    )
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