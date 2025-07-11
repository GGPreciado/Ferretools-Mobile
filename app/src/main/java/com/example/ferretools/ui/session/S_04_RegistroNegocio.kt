package com.example.ferretools.ui.session

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.theme.FerretoolsTheme
import com.example.ferretools.viewmodel.session.RegistroNegocioViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.ferretools.R

@Composable
fun S_04_RegistroNegocio(
    navController: NavController,
    isLoading: Boolean = false,
    registroNegocioViewModel: RegistroNegocioViewModel = viewModel()
) {

    // Define un launcher para elegir fotos
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        registroNegocioViewModel.updateLogoUri(uri)
    }

    val registroNegocioUiState = registroNegocioViewModel.uiState.collectAsState()
    val navController = navController

    LaunchedEffect(registroNegocioUiState.value.registerSuccessful) {
        if (registroNegocioUiState.value.registerSuccessful) {
            navController.navigate(AppRoutes.Admin.DASHBOARD) {
                popUpTo(AppRoutes.Auth.WELCOME) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                    contentDescription = stringResource(R.string.registro_negocio_volver),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.registro_negocio_detalles),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceDim)
                .clickable {
                    launcher.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (registroNegocioUiState.value.logoUri != null) {
                val painter = rememberAsyncImagePainter(
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(data = registroNegocioUiState.value.logoUri)
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = stringResource(R.string.registro_negocio_imagen_perfil),
                    modifier = Modifier.size(90.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.registro_negocio_agregar_logo),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        BusinessFormField(
            label = stringResource(R.string.registro_negocio_nombre),
            value = registroNegocioUiState.value.businessName,
            onValueChange = { registroNegocioViewModel.updateBusinessName(it) },
            placeholder = stringResource(R.string.registro_negocio_nombre_empresa)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BusinessFormField(
            label = stringResource(R.string.registro_negocio_rubro),
            value = registroNegocioUiState.value.businessType,
            onValueChange = { registroNegocioViewModel.updateBusinessType(it) },
            placeholder = stringResource(R.string.registro_negocio_rubro_placeholder)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BusinessFormField(
            label = stringResource(R.string.registro_negocio_direccion),
            value = registroNegocioUiState.value.address,
            onValueChange = { registroNegocioViewModel.updateAddress(it) },
            placeholder = stringResource(R.string.registro_negocio_direccion)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BusinessFormField(
            label = stringResource(R.string.registro_negocio_ruc),
            value = registroNegocioUiState.value.ruc,
            onValueChange = { registroNegocioViewModel.updateRuc(it) },
            placeholder = stringResource(R.string.registro_negocio_ruc)
        )

        // Mostrar error si existe
        registroNegocioUiState.value.error?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Guardar negocio
                registroNegocioViewModel.registerBusiness()
                /*
                // Ir a la pantalla de HOME administrador
                navController.navigate(AppRoutes.Admin.DASHBOARD)
                 */
            },
            enabled = registroNegocioUiState.value.isFormValid && !isLoading && !registroNegocioUiState.value.registerSuccessful,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
            shape = MaterialTheme.shapes.small,
            elevation = ButtonDefaults.buttonElevation(4.dp)
        ) {
            Text(
                //text = "FINALIZAR REGISTRO",
                text = if (registroNegocioUiState.value.registerSuccessful) stringResource(R.string.registro_negocio_exitoso) else stringResource(R.string.registro_negocio_finalizar),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun BusinessFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun S_04_RegistroNegocioPreview() {
    FerretoolsTheme {
        val navController = rememberNavController()
        S_04_RegistroNegocio(
            navController = navController
        )
    }

}