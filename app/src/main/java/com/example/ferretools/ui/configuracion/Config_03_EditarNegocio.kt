package com.example.ferretools.ui.configuracion

import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.ferretools.viewmodel.configuracion.EditarNegocioViewModel

@Composable
fun Config_03_EditarNegocio(
    navController: NavController,
    isLoading: Boolean = false,
    editarNegocioViewModel: EditarNegocioViewModel = viewModel()
) {
    val editarNegocioUiState = editarNegocioViewModel.uiState.collectAsState()

    // Define un launcher para elegir fotosy guardarla en un archivo temporal
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        editarNegocioViewModel.actualizarFotoUri(uri)
    }

    // LaunchedEffect que se activa cuando edicionExitosa cambia a true, regresa a configuración
    LaunchedEffect(editarNegocioUiState.value.edicionExitosa) {
        if (editarNegocioUiState.value.edicionExitosa) {
            navController.popBackStack()
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
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        Text(
            text = "Editar Datos del Negocio",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        BusinessImage(
            fotoRemotaUrl = editarNegocioUiState.value.fotoRemotaUrl,
            fotoLocalUri = editarNegocioUiState.value.fotoLocalUri,
            onClick = {
                launcher.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        EditBusinessFormField(
            label= "Nombre del negocio",
            value = editarNegocioUiState.value.nombreNegocio,
            onValueChange = { editarNegocioViewModel.actualizarNombreNegocio(it) },
            placeholder = "Nombre de la Empresa",
            isError = editarNegocioUiState.value.errorNombre != null,
            errorText = editarNegocioUiState.value.errorNombre

        )
        Spacer(modifier = Modifier.height(16.dp))

        EditBusinessFormField(
            label = "Rubro del negocio",
            value = editarNegocioUiState.value.tipoNegocio,
            onValueChange = { editarNegocioViewModel.actualizarTipoNegocio(it) },
            placeholder = "Ferretería, farmacia, etc.",
            isError = editarNegocioUiState.value.errorTipo != null,
            errorText = editarNegocioUiState.value.errorTipo
        )
        Spacer(modifier = Modifier.height(16.dp))

        EditBusinessFormField(
            label = "Dirección",
            value = editarNegocioUiState.value.direccionNegocio,
            onValueChange = { editarNegocioViewModel.actualizarDireccionNegocio(it) },
            placeholder = "Dirección del negocio",
            isError = editarNegocioUiState.value.errorDireccion != null,
            errorText = editarNegocioUiState.value.errorDireccion
        )
        Spacer(modifier = Modifier.height(16.dp))

        EditBusinessFormField(
            label = "RUC",
            value = editarNegocioUiState.value.ruc,
            onValueChange = { editarNegocioViewModel.actualizarRuc(it) },
            placeholder = "RUC",
            keyboardType = KeyboardType.Number,
            isError = editarNegocioUiState.value.errorRuc != null,
            errorText = editarNegocioUiState.value.errorRuc
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                editarNegocioViewModel.editarNegocio()
            },
            enabled = editarNegocioUiState.value.formsValido && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(2.dp)
        ) {
            Text("GUARDAR", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun BusinessImage(fotoRemotaUrl: String?, fotoLocalUri: Uri?, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageToDisplay: Any? = fotoLocalUri ?: fotoRemotaUrl

    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceDim)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageToDisplay != null) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(imageToDisplay)
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = "Imagen del negocio",
                modifier = Modifier.size(90.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Agregar imagen del negocio",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(54.dp)
            )
        }
    }
}

@Composable
fun EditBusinessFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorText: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = VisualTransformation.None,
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun Config_03_EditarNegocioPreview() {
//    val navController = rememberNavController()
//    Config_03_EditarNegocio(
//        navController = navController
//    )
//}