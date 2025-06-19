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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.ferretools.viewmodel.configuracion.EditarPerfilViewModel

@Composable
fun Config_02_EditarPerfil(
    navController: NavController,
    isLoading: Boolean = false,
    editarPefilViewModel: EditarPerfilViewModel = viewModel()
) {
    val editarPerfilUiState = editarPefilViewModel.uiState.collectAsState()

    // Define un launcher para elegir fotos
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        editarPefilViewModel.updateFotoUri(uri)
    }

    // Evento que se lanza cuando se logra guardar la información en FB Auth y Firestore
    if (editarPerfilUiState.value.editSuccessful) {
        if (editarPerfilUiState.value.emailEdited) {
            EmailUpdateDialog(
                onConfirm = { navController.popBackStack() },
                onDismiss = { navController.popBackStack() },
            )
        } else {
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        Text(
            text = "Editar Perfil",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        ProfileImage(
            imageRemoteUrl = editarPerfilUiState.value.imageRemoteUrl,
            imageLocalUri = editarPerfilUiState.value.imageLocalUri,
            onClick = {
                launcher.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        EditProfileFormField(
            label = "Nombre",
            value = editarPerfilUiState.value.name,
            onValueChange = { editarPefilViewModel.updateName(it) },
            placeholder = "Ingrese sus nombres",
            isError = editarPerfilUiState.value.nameError != null,
            errorText = editarPerfilUiState.value.nameError
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditProfileFormField(
            label = "Teléfono",
            value = editarPerfilUiState.value.phone,
            onValueChange = { editarPefilViewModel.updatePhone(it) },
            placeholder = "Teléfono",
            keyboardType = KeyboardType.Phone,
            isError = editarPerfilUiState.value.phoneError != null,
            errorText = editarPerfilUiState.value.phoneError
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditProfileFormField(
            label = "Correo electrónico",
            value = editarPerfilUiState.value.email,
            onValueChange = { editarPefilViewModel.updateEmail(it) },
            placeholder = "Correo",
            keyboardType = KeyboardType.Email,
            isError = editarPerfilUiState.value.emailError != null,
            errorText = editarPerfilUiState.value.emailError
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = editarPerfilUiState.value.isFormValid && !isLoading,
            onClick = {
                editarPefilViewModel.editProfile()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ),
            elevation = ButtonDefaults.buttonElevation(4.dp)
        ) {
            Text("GUARDAR", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun ProfileImage(imageRemoteUrl: String?, imageLocalUri: Uri?, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageToDisplay: Any? = imageLocalUri ?: imageRemoteUrl

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
                contentDescription = "Imagen de perfil",
                modifier = Modifier.size(90.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Agregar imagen de perfil",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(54.dp)
            )
        }
    }
}

@Composable
fun EditProfileFormField(
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
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
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

@Composable
fun EmailUpdateDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Aceptar")
            }
        },
        title = { Text("Cambio de correo electrónico") },
        text = {
            Column {
                Text("Se le ha enviado un correo para que confirme los cambios. Acéptelo y los cambios se harán efectivos desde el siguiente inicio de sesión." )
            }
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun Config_02_EditarPerfilPreview() {
//    FerretoolsTheme {
//        val navController = rememberNavController()
//        Config_02_EditarPerfil(
//            navController = navController,
//            initialName = "Juan",
//            initialLastName = "Pérez",
//            initialPhone = "987654321",
//            initialEmail = "juan.perez@email.com"
//        )
//    }
//}

