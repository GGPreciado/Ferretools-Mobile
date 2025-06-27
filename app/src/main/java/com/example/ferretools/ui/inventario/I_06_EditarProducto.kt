package com.example.ferretools.ui.inventario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.R
import com.example.ferretools.model.database.Producto
import androidx.compose.foundation.layout.Arrangement
import com.example.ferretools.viewmodel.inventario.AgregarProductoViewModel
import com.example.ferretools.viewmodel.inventario.ListaCategoriasViewModel

@Composable
fun I_06_EditarProducto(
    navController: NavController,
    viewModel: AgregarProductoViewModel = viewModel(),
    categoriaViewModel: ListaCategoriasViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val categorias = categoriaViewModel.uiState.collectAsState().value.categorias
    val scrollState = rememberScrollState()
    val showDialog = remember { mutableStateOf(false) }
    val showErrorDialog = remember { mutableStateOf(false) }
    val showCategoriaDialog = remember { mutableStateOf(false) }

    // Obtener el producto seleccionado del manager
    val productoSeleccionado = ProductoSeleccionadoManager.obtenerProducto()

    if (productoSeleccionado == null) {
        // Mostrar mensaje si no hay producto seleccionado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No hay producto seleccionado",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Volver")
            }
        }
        return
    }

    // Estados para los campos del formulario
    var nombreProducto by remember { mutableStateOf(productoSeleccionado.nombre) }
    var precioProducto by remember { mutableStateOf(productoSeleccionado.precio.toString()) }
    var cantidadProducto by remember { mutableStateOf(productoSeleccionado.cantidad_disponible.toString()) }
    var descripcionProducto by remember { mutableStateOf(productoSeleccionado.descripcion ?: "") }
    var categoriaSeleccionada by remember { mutableStateOf("") } // TODO: Obtener nombre de categoría
    var expanded by remember { mutableStateOf(false) }

    // Producto original para comparar cambios
    val productoOriginal = productoSeleccionado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
            }
            Text(
                text = "Editar Producto",
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
                    Image(
                        painter = painterResource(id = R.drawable.cargar_imagen),
                        contentDescription = "Cargar imagen",
                    )
                    Text(
                        text = "Cambiar imagen",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Código de barras (no editable)
            Text("Código de barras", fontWeight = FontWeight.Bold)
            TextField(
                value = productoOriginal.codigo_barras,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = false,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color(0xFFE0E0E0),
                    disabledContainerColor = Color(0xFFCCCCCC)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Nombre de producto
            Text("Nombre de Producto", fontWeight = FontWeight.Bold)
            TextField(
                value = nombreProducto,
                onValueChange = { nombreProducto = it },
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
                value = precioProducto,
                onValueChange = { precioProducto = it },
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
                value = cantidadProducto,
                onValueChange = { cantidadProducto = it },
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
            
            // Categoría
            Text("Categoría", fontWeight = FontWeight.Bold)
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFE0E0E0)
                    )
                ) {
                    Text(
                        text = if (categoriaSeleccionada.isNotEmpty()) categoriaSeleccionada else "Seleccionar categoría",
                        color = if (categoriaSeleccionada.isNotEmpty()) Color.Black else Color.Gray
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
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
                                categoriaSeleccionada = cat.nombre
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Descripción
            Text("Descripción", fontWeight = FontWeight.Bold)
            TextField(
                value = descripcionProducto,
                onValueChange = { descripcionProducto = it },
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
                    val nombre = nombreProducto.trim()
                    val precio = precioProducto.toDoubleOrNull() ?: 0.0
                    val cantidad = cantidadProducto.toIntOrNull() ?: 0
                    val descripcion = descripcionProducto.trim()
                    val categoria = categoriaSeleccionada.trim()
                    
                    val categoriaId = if (categoria.isNotEmpty()) {
                        categorias.find { it.nombre == categoria }?.id ?: productoOriginal.categoria_id
                    } else {
                        productoOriginal.categoria_id
                    }
                    
                    if (nombre.isNotEmpty() && precio > 0 && cantidad >= 0 && categoriaId != null) {
                        // Crear producto editado
                        val productoEditado = Producto(
                            producto_id = productoOriginal.producto_id,
                            nombre = nombre,
                            descripcion = if (descripcion.isNotEmpty()) descripcion else null,
                            precio = precio,
                            cantidad_disponible = cantidad,
                            codigo_barras = productoOriginal.codigo_barras,
                            imagen_url = null,
                            categoria_id = categoriaId,
                            negocio_id = productoOriginal.negocio_id
                        )
                        
                        println("DEBUG: Guardando cambios del producto: ${productoEditado.nombre}")
                        viewModel.editarProducto(productoOriginal, productoEditado) { exito ->
                            if (exito) {
                                showDialog.value = true
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
                Text("Guardar cambios", color = Color.White, fontSize = 18.sp)
            }
        }
        
        // Diálogo de confirmación
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Producto Actualizado") },
                text = { Text("El producto ha sido actualizado correctamente.") },
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
                                categoriaSeleccionada = nuevaCategoria.trim()
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun I_06_EditarProductoPreview() {
    val navController = rememberNavController()
    I_06_EditarProducto(
        navController = navController
    )
}
