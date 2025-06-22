package com.example.ferretools.ui.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun I_09_CrearCategoria(
    navController: NavController,
    viewModel: InventarioViewModel = viewModel()
) {
    val showDialog = remember { mutableStateOf(false) }
    val nombreCategoria = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header fijo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF22D366))
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.Black
                )
            }
            Text(
                text = "Crear Categoría",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        // Campo de texto
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Nombre de la categoría", fontWeight = FontWeight.Bold)
            TextField(
                value = nombreCategoria.value,
                onValueChange = { nombreCategoria.value = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color(0xFFE0E0E0)
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (nombreCategoria.value.isNotBlank()) {
                        viewModel.agregarCategoria(nombreCategoria.value)
                        showDialog.value = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Crear categoría", color = Color.White, fontSize = 18.sp)
            }
        }
        // Diálogo de confirmación
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Categoría Creada") },
                text = { Text("La categoría ha sido creada correctamente.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun I_09_CrearCategoriaPreview() {
    val navController = rememberNavController()
    I_09_CrearCategoria(navController = navController)
}