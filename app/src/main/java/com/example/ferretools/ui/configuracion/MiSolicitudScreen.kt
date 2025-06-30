package com.example.ferretools.ui.configuracion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ferretools.viewmodel.configuracion.MiSolicitudUiState
import com.example.ferretools.viewmodel.configuracion.MiSolicitudViewModel

@Composable
fun MiSolicitudScreen(
    navController: NavController,
    viewModel: MiSolicitudViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF333333)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Text(
            text = "Mi Solicitud",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        when (uiState) {
            is MiSolicitudUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MiSolicitudUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text((uiState as MiSolicitudUiState.Error).message, color = Color.Red)
                }
            }
            is MiSolicitudUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No tienes ninguna solicitud pendiente.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.crearSolicitud(
                                    onSuccess = {
                                        successMessage = "Solicitud creada exitosamente"
                                    },
                                    onError = {
                                        errorMessage = it
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Solicitar ser Almacenero", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            is MiSolicitudUiState.Success -> {
                val solicitud = (uiState as MiSolicitudUiState.Success).solicitud
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Foto",
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF757575)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(solicitud.nombreUsuario, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(solicitud.correo, fontSize = 14.sp, color = Color.Gray)
                            Text("Rol solicitado: ${solicitud.rolSolicitado}", fontSize = 14.sp)
                            Text("Estado: ${solicitud.estado}", fontSize = 14.sp)
                        }
                    }
                }
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar Solicitud", fontWeight = FontWeight.Bold)
                }
            }
        }
        if (showDialog && uiState is MiSolicitudUiState.Success) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Cancelar Solicitud") },
                text = { Text("¿Estás seguro de que deseas cancelar tu solicitud?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.cancelarSolicitud(
                                onSuccess = {
                                    successMessage = "Solicitud cancelada"
                                    showDialog = false
                                },
                                onError = {
                                    errorMessage = it
                                    showDialog = false
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Sí, cancelar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        successMessage?.let {
            Text(
                text = it,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
} 