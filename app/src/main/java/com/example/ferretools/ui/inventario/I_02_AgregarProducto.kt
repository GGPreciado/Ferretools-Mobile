package com.example.ferretools.ui.inventario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ferretools.R
import com.example.ferretools.model.database.Producto
import com.example.ferretools.theme.primaryContainerLight
import com.example.ferretools.utils.SesionUsuario

@Composable
fun I_02_AgregarProducto(
    navController: NavController,
    viewModel: ProductoViewModel,
    firestoreViewModel: InventarioFirestoreViewModel
) {
    val scrollState = rememberScrollState()
    val showDialog = remember { mutableStateOf(false) }
    val showErrorDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header fijo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = primaryContainerLight)
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
            }
            Text(
                text = "Agregar Producto",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        // Formulario desplazable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón cargar imagen
            Button(
                onClick = { /* TODO: cargar imagen */ },
                modifier = Modifier
                    .size(width = 200.dp, height = 60.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // Aquí agregas el ícono o la imagen
                    Image(
                        painter = painterResource(id = R.drawable.cargar_imagen), // Cambia el nombre según tu archivo
                        contentDescription = "Cargar ",
                        )


                    Text(
                        text = "Cargar ",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,

                        )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Código de barras
            Text("Codigo de barras", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = viewModel.codigoBarras.value,
                    onValueChange = { viewModel.codigoBarras.value = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color(0xFFE0E0E0)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { /* TODO: escanear */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.escaner), // Cambia el nombre según tu archivo
                        contentDescription = "Cargar Imagen",
                    )

                   // Icon(Icons.Default.List, contentDescription = "Escanear", tint = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Nombre de producto
            Text("Nombre de Producto", fontWeight = FontWeight.Bold)
            TextField(
                value = viewModel.nombreProducto.value,
                onValueChange = { viewModel.nombreProducto.value = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color(0xFFE0E0E0)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Precio
            Text("Precio", fontWeight = FontWeight.Bold)
            TextField(
                value = viewModel.precio.value,
                onValueChange = { viewModel.precio.value = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color(0xFFE0E0E0)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Cantidad disponible
            Text("Cantidad disponible", fontWeight = FontWeight.Bold)
            TextField(
                value = viewModel.cantidad.value,
                onValueChange = { viewModel.cantidad.value = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color(0xFFE0E0E0)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Categoría (Dropdown)
            Text("Categoria", fontWeight = FontWeight.Bold)
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (viewModel.categoriaSeleccionada.value.isEmpty()) "Selecciona una categoría"
                        else viewModel.categoriaSeleccionada.value
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    viewModel.categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = {
                                viewModel.categoriaSeleccionada.value = cat.nombre
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Descripción
            Text("Descripcion", fontWeight = FontWeight.Bold)
            TextField(
                value = viewModel.descripcion.value,
                onValueChange = { viewModel.descripcion.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color(0xFFE0E0E0)
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        // Botón grande fijo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    // Validación básica
                    val nombre = viewModel.nombreProducto.value.trim()
                    val precio = viewModel.precio.value.toDoubleOrNull() ?: 0.0
                    val cantidad = viewModel.cantidad.value.toIntOrNull() ?: 0
                    val descripcion = viewModel.descripcion.value.trim()
                    val codigoBarras = viewModel.codigoBarras.value.trim()
                    val categoria = viewModel.categoriaSeleccionada.value.trim()
                    if (nombre.isNotEmpty() && precio > 0 && cantidad >= 0 && categoria.isNotEmpty()) {
                        val producto = Producto(
                            nombre = nombre,
                            descripcion = if (descripcion.isNotEmpty()) descripcion else null,
                            precio = precio,
                            cantidad_disponible = cantidad,
                            codigo_barras = codigoBarras,
                            imagen_url = null, // Puedes agregar lógica para imagen luego
                            categoria_id = categoria,
                            negocio_id = SesionUsuario.usuario?.negocioId!!
                        )
                        firestoreViewModel.agregarProducto(producto) { exito ->
                            if (exito) {
                                showDialog.value = true
                                viewModel.limpiarFormulario()
                            } else {
                                showErrorDialog.value = true
                            }
                        }
                    } else {
                        showErrorDialog.value = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Agregar producto", color = Color.White, fontSize = 18.sp)
            }
        }
        // Diálogo de confirmación
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Producto Agregado") },
                text = { Text("El producto ha sido agregado correctamente.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
        // Diálogo de error
        if (showErrorDialog.value) {
            AlertDialog(
                onDismissRequest = { showErrorDialog.value = false },
                title = { Text("Error") },
                text = { Text("Por favor, completa todos los campos obligatorios correctamente.") },
                confirmButton = {
                    Button(onClick = { showErrorDialog.value = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun I_02_AgregarProductoPreview() {
//    val navController = rememberNavController()
//    val viewModel: ProductoViewModel = viewModel()
//    I_02_AgregarProducto(
//        navController = navController,
//        viewModel = viewModel
//    )
//}