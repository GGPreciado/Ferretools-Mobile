package com.example.ferretools.ui.inventario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.R
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.model.database.Producto
import androidx.compose.foundation.verticalScroll
//import androidx.compose.foundation.layout.weight
//import androidx.compose.foundation.layout.rememberScrollState
import androidx.compose.foundation.rememberScrollState
import com.example.ferretools.viewmodel.inventario.DetallesProductoViewModel

@Composable
fun I_04_DetallesProducto(
    navController: NavController,
    viewModel: DetallesProductoViewModel = viewModel()
) {
    val eliminado = viewModel.eliminado.collectAsState().value
    val showDialog = remember { mutableStateOf(false) }
    val showSuccess = remember { mutableStateOf(false) }
    val showErrorDialog = remember { mutableStateOf(false) }
    
    // Obtener el producto seleccionado del manager
    val producto = ProductoSeleccionadoManager.obtenerProducto()
    
    println("DEBUG: I_04_DetallesProducto - Producto seleccionado: ${producto?.nombre}")
    
    if (producto == null) {
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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header fijo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF00E676))
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
                text = "Detalles de Producto",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 4.dp),

            )
        }
        
        // Contenido desplazable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Imagen
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .size(width = 300.dp, height = 100.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.cargar_imagen),
                        contentDescription = "Cambiar imagen",
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Cambiar imagen",
                        color = Color.Black,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Campos
            DetalleCampo("Codigo de barras", producto.codigo_barras)
            DetalleCampo("Nombre de Producto", producto.nombre)
            DetalleCampo("Precio", "S/ ${producto.precio}")
            DetalleCampo("Cantidad disponible", producto.cantidad_disponible.toString())
            DetalleCampo("Categoria", "Categoría del producto") // TODO: Obtener nombre de categoría
            DetalleCampo("Descripcion", producto.descripcion ?: "Sin descripción", multiline = true)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón de análisis
            Button(
                onClick = { navController.navigate(AppRoutes.Inventory.PRODUCT_REPORT) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
            ) {
                Text("Realizar análisis por producto", color = Color.Black)
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { 
                        showDialog.value = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Eliminar", color = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { 
                        // Navegar a la pantalla de editar producto
                        navController.navigate(AppRoutes.Inventory.EDIT_PRODUCT)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Diálogo de confirmación de eliminación
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar este producto?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarProducto(producto.producto_id)
                        showDialog.value = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de éxito
    if (showSuccess.value) {
        AlertDialog(
            onDismissRequest = { showSuccess.value = false },
            title = { Text("Éxito") },
            text = { Text("Producto eliminado correctamente") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccess.value = false
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
            text = { Text("No se pudo eliminar el producto") },
            confirmButton = {
                Button(onClick = { showErrorDialog.value = false }) {
                    Text("OK")
                }
            }
        )
    }
    
    // Observar el estado de eliminación
    eliminado?.let { exito ->
        if (exito) {
            showSuccess.value = true
        } else {
            showErrorDialog.value = true
        }
    }
}

@Composable
fun DetalleCampo(label: String, value: String, multiline: Boolean = false) {
    Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (multiline) 64.dp else 32.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Text(value)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewEditarProductoScreen() {
    val navController = rememberNavController()
    I_04_DetallesProducto(navController = navController)
}
