package com.example.ferretools.ui.configuracion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.ferretools.model.database.Solicitud
import com.example.ferretools.viewmodel.configuracion.RevisarSolicitudesViewModel
import com.example.ferretools.viewmodel.configuracion.SolicitudesUiState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun SolicitudesScreen(
    navController: NavController,
    viewModel: RevisarSolicitudesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val solicitudesState by viewModel.solicitudesState.collectAsState()
    var selectedSolicitud by remember { mutableStateOf<Solicitud?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    /*
    LaunchedEffect(Unit) {
        viewModel.setContext(context)
    }*/

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
            text = "Solicitudes de Usuarios",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        when (solicitudesState) {
            is SolicitudesUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SolicitudesUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text((solicitudesState as SolicitudesUiState.Error).message, color = Color.Red)
                }
            }
            is SolicitudesUiState.Success -> {
                val solicitudes = (solicitudesState as SolicitudesUiState.Success).solicitudes
                if (solicitudes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay solicitudes pendientes.")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(solicitudes) { solicitud ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        selectedSolicitud = solicitud
                                        showDialog = true
                                    },
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
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showDialog && selectedSolicitud != null) {
            SolicitudDetailDialog(
                solicitud = selectedSolicitud!!,
                onDismiss = { showDialog = false },
                onAceptar = {
                    viewModel.aceptarSolicitud(selectedSolicitud!!, {
                        successMessage = "Solicitud aceptada"
                        showDialog = false
                    }, {
                        errorMessage = it
                    })
                },
                onRechazar = {
                    viewModel.rechazarSolicitud(selectedSolicitud!!, {
                        successMessage = "Solicitud rechazada"
                        showDialog = false
                    }, {
                        errorMessage = it
                    })
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

@Composable
fun SolicitudDetailDialog(
    solicitud: Solicitud,
    onDismiss: () -> Unit,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalles de la Solicitud") },
        text = {
            Column {
                Text("Nombre: ${solicitud.nombreUsuario}")
                Text("Correo: ${solicitud.correo}")
                Text("Celular: ${solicitud.celular}")
                Text("Rol solicitado: ${solicitud.rolSolicitado}")
            }
        },
        confirmButton = {
            Button(onClick = onAceptar, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onRechazar, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))) {
                Text("Rechazar")
            }
        }
    )
} 