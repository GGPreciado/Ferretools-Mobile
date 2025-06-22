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
import com.example.ferretools.ui.inventario.InventarioFirestoreViewModel
import com.example.ferretools.ui.inventario.CategoriaFirestoreViewModel
import androidx.compose.runtime.collectAsState
import com.example.ferretools.theme.primaryContainerLight
import com.example.ferretools.utils.SesionUsuario

@Composable
fun I_02_AgregarProducto(
    navController: NavController,
    viewModel: ProductoViewModel,
    firestoreViewModel: InventarioFirestoreViewModel,
    categoriaViewModel: CategoriaFirestoreViewModel = viewModel()
) {
    val categorias = categoriaViewModel.categorias.collectAsState().value
    val scrollState = rememberScrollState()
    val showDialog = remember { mutableStateOf(false) }
    val showErrorDialog = remember { mutableStateOf(false) }
    val showCategoriaDialog = remember { mutableStateOf(false) }

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
                        else {
                            val categoria = categorias.find { it.id == viewModel.categoriaSeleccionada.value }
                            categoria?.nombre ?: viewModel.categoriaSeleccionada.value
                        }
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    // Opción para agregar nueva categoría
                    DropdownMenuItem(
                        text = { Text("+ Agregar nueva categoría") },
                        onClick = {
                            showCategoriaDialog.value = true
                            expanded = false
                        }
                    )
                    // Lista de categorías existentes
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = {
                                viewModel.categoriaSeleccionada.value = cat.id
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
                    val categoriaSeleccionada = viewModel.categoriaSeleccionada.value.trim()
                    
                    if (nombre.isNotEmpty() && precio > 0 && cantidad >= 0 && categoriaSeleccionada.isNotEmpty()) {
                        // Si la categoría seleccionada es un ID (categoría existente)
                        if (categorias.any { it.id == categoriaSeleccionada }) {
                            // Es una categoría existente, guardar directamente
                            println("DEBUG: Categoría existente seleccionada - ID: $categoriaSeleccionada")
                            val producto = Producto(
                                nombre = nombre,
                                descripcion = if (descripcion.isNotEmpty()) descripcion else null,
                                precio = precio,
                                cantidad_disponible = cantidad,
                                codigo_barras = codigoBarras,
                                imagen_url = null,
                                categoria_id = categoriaSeleccionada
                            )
                            println("DEBUG: Producto a guardar (categoría existente) - categoria_id: ${producto.categoria_id}")
                            firestoreViewModel.agregarProducto(producto) { exito ->
                                if (exito) {
                                    println("DEBUG: Producto agregado exitosamente, mostrando diálogo de confirmación")
                                    showDialog.value = true
                                    viewModel.limpiarFormulario()
                                } else {
                                    println("ERROR: Error al agregar producto")
                                    showErrorDialog.value = true
                                }
                            }
                        } else {
                            // Es una nueva categoría, crearla primero
                            categoriaViewModel.agregarCategoriaSiNoExiste(categoriaSeleccionada) { categoriaId ->
                                if (categoriaId != null) {
                                    println("DEBUG: Nueva categoría creada - Nombre: $categoriaSeleccionada, ID: $categoriaId")
                                    
                                    val producto = Producto(
                                        nombre = nombre,
                                        descripcion = if (descripcion.isNotEmpty()) descripcion else null,
                                        precio = precio,
                                        cantidad_disponible = cantidad,
                                        codigo_barras = codigoBarras,
                                        imagen_url = null,
                                        categoria_id = categoriaId
                                    )
                                    println("DEBUG: Producto a guardar - categoria_id: ${producto.categoria_id}")
                                    firestoreViewModel.agregarProducto(producto) { exito ->
                                        if (exito) {
                                            println("DEBUG: Producto agregado exitosamente (nueva categoría), mostrando diálogo de confirmación")
                                            showDialog.value = true
                                            viewModel.limpiarFormulario()
                                        } else {
                                            println("ERROR: Error al agregar producto (nueva categoría)")
                                            showErrorDialog.value = true
                                        }
                                    }
                                } else {
                                    println("ERROR: Error al crear categoría")
                                    showErrorDialog.value = true
                                }
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
                            println("DEBUG: Cerrando diálogo y navegando hacia atrás")
                            showDialog.value = false
                            // Forzar recarga antes de navegar
                            firestoreViewModel.recargarProductos()
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
        // Diálogo para agregar nueva categoría
        if (showCategoriaDialog.value) {
            var nuevaCategoria by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showCategoriaDialog.value = false },
                title = { Text("Agregar Nueva Categoría") },
                text = {
                    TextField(
                        value = nuevaCategoria,
                        onValueChange = { nuevaCategoria = it },
                        label = { Text("Nombre de la categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nuevaCategoria.trim().isNotEmpty()) {
                                viewModel.categoriaSeleccionada.value = nuevaCategoria.trim()
                                showCategoriaDialog.value = false
                            }
                        }
                    ) {
                        Text("Agregar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showCategoriaDialog.value = false }) {
                        Text("Cancelar")
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