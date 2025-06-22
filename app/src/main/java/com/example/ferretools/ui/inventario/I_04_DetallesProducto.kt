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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.R
import com.example.ferretools.navigation.AppRoutes

@Composable
fun I_04_DetallesProducto(
    navController: NavController,
    codigoBarras: String = "",
    nombre: String = "",
    precio: String = "",
    cantidad: String = "",
    categoria: String = "",
    descripcion: String = ""
) {
    val showDialog = remember { mutableStateOf(false) }
    val showSuccess = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
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
        Spacer(modifier = Modifier.height(16.dp))
        // Imagen
        Button(
            onClick = { /* TODO */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .size(width = 300.dp, height = 100.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp),


            ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(
                painter = painterResource(id = R.drawable.cargar_imagen), // Cambia el nombre según tu archivo
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
        Spacer(modifier = Modifier.height(16.dp))

        // Campos
        DetalleCampo("Codigo de barras", codigoBarras)
        DetalleCampo("Nombre de Producto", nombre)
        DetalleCampo("Precio", precio)
        DetalleCampo("Cantidad disponible", cantidad)
        DetalleCampo("Categoria", categoria)
        DetalleCampo("Descripcion", descripcion, multiline = true)
        Spacer(modifier = Modifier.height(16.dp))
        // Botón de análisis
        Button(
            onClick = { navController.navigate(AppRoutes.Inventory.PRODUCT_REPORT) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
        ) {
            Text("Realizar análisis por producto", color = Color.Black)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { showDialog.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.weight(1f)
            ) {
                Text("Eliminar", color = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Editar", color = Color.White)
            }
        }
    }

    // Diálogo de confirmación
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar este producto?") },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    showSuccess.value = true
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    // Mensaje de éxito
    if (showSuccess.value) {
        AlertDialog(
            onDismissRequest = {
                showSuccess.value = false
                navController.popBackStack()
            },
            title = { Text("Producto eliminado") },
            text = { Text("El producto ha sido eliminado correctamente.") },
            confirmButton = {
                Button(onClick = {
                    showSuccess.value = false
                    navController.popBackStack()
                }) {
                    Text("Aceptar")
                }
            }
        )
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
