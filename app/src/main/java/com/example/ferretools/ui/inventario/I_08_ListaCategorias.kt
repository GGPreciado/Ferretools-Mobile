package com.example.ferretools.ui.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.viewmodel.inventario.ListaCategoriasViewModel
import com.example.ferretools.model.database.Categoria

@Composable
fun I_08_ListaCategorias(
    navController: NavController,
    viewModel: ListaCategoriasViewModel = viewModel(),
    isReadOnly: Boolean = false
) {
    val uiState = viewModel.uiState.collectAsState().value
    val scrollState = rememberScrollState()
    val showEditDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val categoriaSeleccionada = remember { mutableStateOf<Categoria?>(null) }
    val nuevoNombre = remember { mutableStateOf("") }
    val showSuccessDialog = remember { mutableStateOf(false) }
    val showErrorDialog = remember { mutableStateOf(false) }
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
                text = "Categorías",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        // Barra de búsqueda
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: menú */ }) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Menú",
                    tint = Color.Black
                )
            }
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.weight(1f),
                placeholder = { Text("Buscar categoría") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5)
                )
            )
        }
        // Botón grande (crear categoría)
        if (!isReadOnly) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    onClick = { navController.navigate(AppRoutes.Inventory.ADD_CATEGORY) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Crear nueva categoría", color = Color.White, fontSize = 18.sp)
                }
            }
        }
        // Lista de categorías desplazable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            uiState.categorias.forEach { categoria ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { 
                            val route = if (isReadOnly) {
                                "inventory_category_details/${categoria.id}?isReadOnly=true"
                            } else {
                                "inventory_category_details/${categoria.id}"
                            }
                            navController.navigate(route)
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Ver productos",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(categoria.nombre, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        if (!isReadOnly) {
                            // Botón Editar
                            IconButton(
                                onClick = {
                                    categoriaSeleccionada.value = categoria
                                    nuevoNombre.value = categoria.nombre
                                    showEditDialog.value = true
                                }
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = Color.Blue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            // Botón Eliminar
                            IconButton(
                                onClick = {
                                    categoriaSeleccionada.value = categoria
                                    showDeleteDialog.value = true
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Diálogos de editar/eliminar solo si !isReadOnly
        if (!isReadOnly) {
            // Diálogo de editar categoría
            if (showEditDialog.value && categoriaSeleccionada.value != null) {
                AlertDialog(
                    onDismissRequest = { showEditDialog.value = false },
                    title = { Text("Editar Categoría") },
                    text = {
                        TextField(
                            value = nuevoNombre.value,
                            onValueChange = { nuevoNombre.value = it },
                            label = { Text("Nombre de la categoría") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                categoriaSeleccionada.value?.let { categoria ->
                                    viewModel.editarCategoria(categoria.id, nuevoNombre.value) { exito ->
                                        if (exito) {
                                            showSuccessDialog.value = true
                                            showEditDialog.value = false
                                        } else {
                                            showErrorDialog.value = true
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showEditDialog.value = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
            
            // Diálogo de confirmación para eliminar
            if (showDeleteDialog.value && categoriaSeleccionada.value != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog.value = false },
                    title = { Text("Eliminar Categoría") },
                    text = { Text("¿Estás seguro de que quieres eliminar la categoría '${categoriaSeleccionada.value?.nombre}'?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                categoriaSeleccionada.value?.let { categoria ->
                                    viewModel.eliminarCategoria(categoria.id) { exito ->
                                        if (exito) {
                                            showSuccessDialog.value = true
                                            showDeleteDialog.value = false
                                        } else {
                                            showErrorDialog.value = true
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDeleteDialog.value = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
        
        // Diálogo de éxito
        if (showSuccessDialog.value) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog.value = false },
                title = { Text("Éxito") },
                text = { Text("Operación completada correctamente.") },
                confirmButton = {
                    Button(onClick = { showSuccessDialog.value = false }) {
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
                text = { Text("No se pudo completar la operación. Inténtalo de nuevo.") },
                confirmButton = {
                    Button(onClick = { showErrorDialog.value = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun I_08_ListaCategoriasPreview() {
    val navController = rememberNavController()
    I_08_ListaCategorias(navController = navController)
}