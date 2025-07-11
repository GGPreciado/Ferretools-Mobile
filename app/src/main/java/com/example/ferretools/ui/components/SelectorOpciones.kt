package com.example.ferretools.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SelectorOpciones(
    opcion1: String,
    opcion1Img: Int? = null,
    opcion2: String,
    opcion2Img: Int? = null,
    seleccionado: String,
    onSeleccionar: (String) -> Unit
) {
    val colorSeleccionado = Color(0xFFB39DDB) // Más notorio
    val colorNoSeleccionado = Color(0xFFFCEFFE)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
    ) {
        // Botón Opción 1
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    if (seleccionado == opcion1) colorSeleccionado else colorNoSeleccionado,
                    RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                )
                .border(
                    width = if (seleccionado == opcion1) 2.dp else 0.dp,
                    color = if (seleccionado == opcion1) Color(0xFF512DA8) else Color.Transparent,
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                )
                .clickable { onSeleccionar(opcion1) },
            contentAlignment = Alignment.Center
        ) {
            if (opcion1Img != null) {
                Image(
                    painter = painterResource(opcion1Img),
                    contentDescription = opcion1,
                    Modifier.size(40.dp)
                )
            } else {
                Text(
                    text = opcion1,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = if (seleccionado == opcion1) Color(0xFF512DA8) else Color.DarkGray,
                    style = if (seleccionado == opcion1) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
                )
            }
        }
        // Divisor vertical
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.Black)
        )
        // Botón Opción 2
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    if (seleccionado == opcion2) colorSeleccionado else colorNoSeleccionado,
                    RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                )
                .border(
                    width = if (seleccionado == opcion2) 2.dp else 0.dp,
                    color = if (seleccionado == opcion2) Color(0xFF512DA8) else Color.Transparent,
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                )
                .clickable { onSeleccionar(opcion2) },
            contentAlignment = Alignment.Center
        ) {
            if (opcion2Img != null) {
                Image(
                    painter = painterResource(opcion2Img),
                    contentDescription = opcion2,
                    Modifier.size(40.dp)
                )
            } else {
                Text(
                    text = opcion2,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = if (seleccionado == opcion2) Color(0xFF512DA8) else Color.DarkGray,
                    style = if (seleccionado == opcion2) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}