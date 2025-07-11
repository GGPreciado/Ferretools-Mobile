package com.example.ferretools.ui.session

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.theme.FerretoolsTheme
import com.example.ferretools.viewmodel.session.RecuperarContrasenaViewModel
import com.example.ferretools.R

@Composable
fun S_06_RecuperarContrasena(
    navController: NavController,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    viewModel: RecuperarContrasenaViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    // Navegar a la pantalla de cambio de contraseña cuando el código sea válido
    LaunchedEffect(uiState.value.isCodeValid) {
        if (uiState.value.isCodeValid) {
            navController.navigate(AppRoutes.Auth.CHANGE_PASSWORD)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Botón de retroceso
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.recuperar_contrasena_volver),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Spacer(modifier = Modifier.width(48.dp)) // Para equilibrar el espacio del botón
        }
        Text(
            text = stringResource(R.string.recuperar_contrasena_titulo),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.recuperar_contrasena_mensaje),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        // Grupo de correo electrónico
        if (!uiState.value.codeSent) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = stringResource(R.string.recuperar_contrasena_ingrese_correo),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            OutlinedTextField(
                value = uiState.value.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text(stringResource(R.string.recuperar_contrasena_ingrese_correo)) },
                placeholder = { Text(stringResource(R.string.recuperar_contrasena_correo)) },
                singleLine = true,
                isError = uiState.value.emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 20.dp)
            )
            if (uiState.value.emailError != null) {
                Text(
                    text = uiState.value.emailError!!,
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Botón "Enviar código"
            TextButton(
                onClick = { viewModel.sendVerificationCode() },
                enabled = uiState.value.emailError == null && !isLoading,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.recuperar_contrasena_enviar_codigo),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }

        if (uiState.value.codeSent) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Grupo de código de verificación
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = stringResource(R.string.recuperar_contrasena_escriba_codigo),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            OutlinedTextField(
                value = uiState.value.code,
                onValueChange = { viewModel.updateCode(it) },
                label = { Text(stringResource(R.string.recuperar_contrasena_escriba_codigo)) },
                placeholder = { Text(stringResource(R.string.recuperar_contrasena_codigo)) },
                singleLine = true,
                isError = uiState.value.codeError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 20.dp)
            )
            if (uiState.value.codeError != null) {
                Text(
                    text = uiState.value.codeError!!,
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Botón principal "Continuar"
            Button(
                onClick = { viewModel.verifyCode() },
                enabled = uiState.value.codeError == null && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.small,
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.recuperar_contrasena_continuar),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        if (uiState.value.errorMessage != null) {
            Text(
                text = uiState.value.errorMessage!!,
                color = MaterialTheme.colorScheme.onError,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun S_06_RecuperarContrasenaPreview() {
    FerretoolsTheme {
        val navController = rememberNavController()
        S_06_RecuperarContrasena(navController = navController)
    }
}