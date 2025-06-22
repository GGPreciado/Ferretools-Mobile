package com.example.ferretools.ui.session

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.theme.FerretoolsTheme
import com.example.ferretools.viewmodel.session.ElegirNegocioViewModel
import com.example.ferretools.viewmodel.session.NegociosUiState

// Modelo simple para negocio
data class NegocioUi(val id: String, val nombre: String, val logoUrl: String?)

@Composable
fun S_03_02_ElegirNegocio(
    navController: NavController,
    rolUsuario: RolUsuario,
    elegirNegocioViewModel: ElegirNegocioViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedNegocioId by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoadingAfiliar by remember { mutableStateOf(false) }

    val negociosState by elegirNegocioViewModel.negociosState.collectAsState()

    // Navegación tras afiliarse
    fun navegarADashboard() {
        when (rolUsuario) {
            RolUsuario.ADMIN -> navController.navigate(AppRoutes.Admin.DASHBOARD) { popUpTo(AppRoutes.Auth.WELCOME) { inclusive = true } }
            RolUsuario.CLIENTE -> navController.navigate(AppRoutes.Client.DASHBOARD) { popUpTo(AppRoutes.Auth.WELCOME) { inclusive = true } }
            RolUsuario.ALMACENERO -> navController.navigate(AppRoutes.Employee.DASHBOARD) { popUpTo(AppRoutes.Auth.WELCOME) { inclusive = true } }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (rolUsuario == RolUsuario.ADMIN) {
                Button(
                    onClick = { navController.navigate(AppRoutes.Auth.REGISTER_BUSINESS) },
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    shape = MaterialTheme.shapes.small,
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Icon(Icons.Filled.AddBusiness, contentDescription = "Agregar Negocio")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar Negocio", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Elegir Negocio",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar negocio", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        Spacer(modifier = Modifier.height(16.dp))

        when (negociosState) {
            is NegociosUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is NegociosUiState.Error -> {
                Text(
                    text = (negociosState as NegociosUiState.Error).message,
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }
            is NegociosUiState.Success -> {
                val negocios = (negociosState as NegociosUiState.Success).negocios
                val negociosFiltrados = negocios.filter {
                    it.nombre.contains(searchQuery.text, ignoreCase = true)
                }
                if (negociosFiltrados.isEmpty()) {
                    Text(
                        text = "No se encontraron negocios",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                } else {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        negociosFiltrados.forEach { negocio ->
                            NegocioListItem(
                                negocio = negocio,
                                selected = negocio.id == selectedNegocioId,
                                onClick = { selectedNegocioId = negocio.id }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onError,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                val negocios = (negociosState as? NegociosUiState.Success)?.negocios ?: emptyList()
                val negocioSeleccionado = negocios.find { it.id == selectedNegocioId }
                if (negocioSeleccionado != null) {
                    isLoadingAfiliar = true
                    errorMessage = null
                    elegirNegocioViewModel.afiliarUsuarioANegocio(
                        negocioId = negocioSeleccionado.id,
                        onSuccess = {
                            isLoadingAfiliar = false
                            navegarADashboard()
                        },
                        onError = {
                            isLoadingAfiliar = false
                            errorMessage = it
                        }
                    )
                }
            },
            enabled = selectedNegocioId != null && !isLoadingAfiliar && negociosState is NegociosUiState.Success,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.small,
            elevation = ButtonDefaults.buttonElevation(4.dp)
        ) {
            if (isLoadingAfiliar) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("AFILIARSE", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun NegocioListItem(
    negocio: NegocioUi,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(negocio.logoUrl)
                .build()
        )
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceDim),
            contentAlignment = Alignment.Center
        ) {
            if (negocio.logoUrl != null) {
                Image(
                    painter = painter,
                    contentDescription = "Logo del negocio",
                    modifier = Modifier.size(48.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AddBusiness,
                    contentDescription = "Logo por defecto",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = negocio.nombre,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Visibility,
                contentDescription = "Seleccionado",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun S_03_02_ElegirNegocioPreview() {
    FerretoolsTheme {
        val navController = rememberNavController()
        S_03_02_ElegirNegocio(
            navController = navController,
            rolUsuario = RolUsuario.CLIENTE
        )
    }
} 