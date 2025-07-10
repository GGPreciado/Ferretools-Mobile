package com.example.ferretools.ui.components.detalles_cv

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CampoFechaSeleccion(modifier: Modifier = Modifier) {
    // Estado para la fecha seleccionada
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val calendar = Calendar.getInstance()
    var fechaSeleccionada by remember { mutableStateOf(dateFormatter.format(calendar.time)) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Dialogo de selección de fecha
    if (mostrarDialogo) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                fechaSeleccionada = dateFormatter.format(calendar.time)
                mostrarDialogo = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Campo de texto que abre el calendario
    OutlinedTextField(
        value = fechaSeleccionada,
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Seleccionar fecha",
                modifier = Modifier.clickable { mostrarDialogo = true }
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { mostrarDialogo = true }
            .padding(
                top = 8.dp,
                bottom = 4.dp
            )
    )
}