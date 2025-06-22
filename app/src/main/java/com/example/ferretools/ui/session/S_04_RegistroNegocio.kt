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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Detalles del negocio",
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
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier.size(90.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = if (registroNegocioUiState.value.logoUri == null)
                        "Agregar logo del negocio" else "Logo del negocio",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        BusinessFormField(
            label = "Nombre del negocio",
            value = registroNegocioUiState.value.businessName,
            onValueChange = { registroNegocioViewModel.updateBusinessName(it) },
            placeholder = "Nombre de la Empresa"
        )

        Spacer(modifier = Modifier.height(16.dp))

        BusinessFormField(
            label = "Rubro del negocio",
            value = registroNegocioUiState.value.businessType,
            onValueChange = { registroNegocioViewModel.updateBusinessType(it) },
            placeholder = "Ferretería, farmacia, etc."
        )

        Spacer(modifier = Modifier.height(16.dp))

        BusinessFormField(
            label = "Dirección",
            value = registroNegocioUiState.value.address,
            onValueChange = { registroNegocioViewModel.updateAddress(it) },
            placeholder ="Dirección"
        )

        Spacer(modifier = Modifier.height(16.dp))

        BusinessFormField(
            label = "R.U.C.",
            value = registroNegocioUiState.value.ruc,
            onValueChange = { registroNegocioViewModel.updateRuc(it) },
            placeholder = "R.U.C."
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Guardar negocio
                registroNegocioViewModel.registerBusiness()
                // Ir a la pantalla de HOME administrador
                navController.navigate(AppRoutes.Admin.DASHBOARD)
            },
            enabled = registroNegocioUiState.value.isFormValid && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
            shape = MaterialTheme.shapes.small,
            elevation = ButtonDefaults.buttonElevation(4.dp)
        ) {
            Text(
                text = "FINALIZAR REGISTRO",
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