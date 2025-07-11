package com.example.ferretools.ui.session

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.ferretools.model.database.Negocio
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.theme.FerretoolsTheme
import com.example.ferretools.viewmodel.session.ElegirNegocioViewModel
import com.example.ferretools.viewmodel.session.NegociosUiState
import com.example.ferretools.utils.SesionUsuario
import kotlin.collections.find
import com.example.ferretools.R

@Composable
fun S_03_02_ElegirNegocio(
    navController: NavController,
    rolUsuario: RolUsuario,
    elegirNegocioViewModel: ElegirNegocioViewModel = viewModel()
) {
    Log.e("DEBUG", "Pantalla ElegirNegocio - rol actual: ${SesionUsuario.usuario?.rol}, rol deseado: ${SesionUsuario.rolDeseado}")
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedNegocioId by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoadingAfiliar by remember { mutableStateOf(false) }

    val negociosState by elegirNegocioViewModel.negociosState.collectAsState()
    val context = LocalContext.current

    // Navegación tras afiliarse
    fun navegarADashboard() {
        /*
        when (SesionUsuario.usuario?.rol!!) {
            RolUsuario.ADMIN -> navController.navigate(AppRoutes.Admin.DASHBOARD) {
                popUpTo(AppRoutes.Auth.WELCOME) {
                    inclusive = true
                }
            }
            RolUsuario.CLIENTE -> navController.navigate(AppRoutes.Client.DASHBOARD) {
                popUpTo(AppRoutes.Auth.WELCOME) {
                    inclusive = true
                }
            }
            RolUsuario.ALMACENERO -> navController.navigate(AppRoutes.Employee.DASHBOARD) {
                popUpTo(AppRoutes.Auth.WELCOME) {
                    inclusive = true
                }
            }
        }

         */
        navController.navigate(AppRoutes.Client.DASHBOARD) {
            popUpTo(AppRoutes.Auth.WELCOME) {
                inclusive = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.elegir_negocio_atras),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = stringResource(R.string.elegir_negocio_titulo),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header con botón para ADMIN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.elegir_negocio_subtitulo),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Botón "Agregar Negocio" solo para ADMIN
                if (rolUsuario == RolUsuario.ADMIN) {
                    Button(
                        onClick = { navController.navigate(AppRoutes.Auth.REGISTER_BUSINESS) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.AddBusiness,
                            contentDescription = stringResource(R.string.elegir_negocio_agregar),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.elegir_negocio_agregar), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(stringResource(R.string.elegir_negocio_buscar)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search)
            )

            // Lista de negocios
            when (val state = negociosState) {
                is NegociosUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is NegociosUiState.Success -> {
                    val negociosFiltrados = if (searchQuery.text.isNotEmpty()) {
                        state.negocios.filter {
                            it.nombre.contains(searchQuery.text, ignoreCase = true)
                        }
                    } else {
                        state.negocios
                    }

                    if (negociosFiltrados.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.text.isNotEmpty())
                                    stringResource(R.string.elegir_negocio_no_encontrados)
                                else
                                    stringResource(R.string.elegir_negocio_no_disponibles),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(negociosFiltrados) { negocio ->
                                NegocioListItem(
                                    negocio = negocio,
                                    selected = selectedNegocioId == negocio.id,
                                    onClick = { selectedNegocioId = negocio.id }
                                )
                            }
                        }
                    }
                }
                is NegociosUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Mensaje de error
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de afiliación
            Button(
                onClick = {
                    val negocios = (negociosState as? NegociosUiState.Success)?.negocios ?: emptyList()
                    val negocioSeleccionado = negocios.find { it.id == selectedNegocioId }
                    isLoadingAfiliar = true
                    errorMessage = null
                    if (negocioSeleccionado != null) {
                        elegirNegocioViewModel.flujoAfiliacionONuevoNegocio(
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
                    } else if (SesionUsuario.rolDeseado == RolUsuario.ADMIN) {
                        // Si el usuario quiere crear un negocio propio
                        isLoadingAfiliar = false
                        navController.navigate(AppRoutes.Auth.REGISTER_BUSINESS)
                    } else {
                        isLoadingAfiliar = false
                        errorMessage = context.getString(R.string.elegir_negocio_error_seleccion)
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
                    Text(stringResource(R.string.elegir_negocio_afiliarse), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun NegocioListItem(
    negocio: Negocio,
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
                    contentDescription = stringResource(R.string.elegir_negocio_logo),
                    modifier = Modifier.size(48.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AddBusiness,
                    contentDescription = stringResource(R.string.elegir_negocio_logo_defecto),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = negocio.nombre,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (negocio.tipo.isNotEmpty()) {
                Text(
                    text = negocio.tipo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Visibility,
                contentDescription = stringResource(R.string.elegir_negocio_seleccionado),
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