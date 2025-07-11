package com.example.ferretools.ui.session

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.ferretools.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.theme.FerretoolsTheme
import com.example.ferretools.viewmodel.session.CambiarContrasenaViewModel

@Composable
fun S_07_CambiarContrasena(
    navController: NavController,
    isLoading: Boolean = false,
    viewModel: CambiarContrasenaViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    // Navegar a la pantalla de inicio de sesión cuando la contraseña se haya cambiado
    LaunchedEffect(uiState.value.passwordChanged) {
        if (uiState.value.passwordChanged) {
            navController.navigate(AppRoutes.Auth.LOGIN) {
                popUpTo(AppRoutes.Auth.LOGIN) { inclusive = true }
            }
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
                    contentDescription = stringResource(R.string.cambiar_contrasena_volver),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Spacer(modifier = Modifier.width(48.dp)) // Para equilibrar el espacio del botón
        }

        Text(
            text = stringResource(R.string.cambiar_contrasena_titulo),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.cambiar_contrasena_ingrese),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        // Campo de nueva contraseña
        OutlinedTextField(
            value = uiState.value.password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text(stringResource(R.string.cambiar_contrasena_nueva)) },
            placeholder = { Text(stringResource(R.string.cambiar_contrasena_nueva)) },
            singleLine = true,
            isError = uiState.value.passwordError != null,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        if (uiState.value.passwordError != null) {
            Text(
                text = uiState.value.passwordError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Campo de confirmar contraseña
        OutlinedTextField(
            value = uiState.value.confirmPassword,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            label = { Text(stringResource(R.string.cambiar_contrasena_confirmar)) },
            placeholder = { Text(stringResource(R.string.cambiar_contrasena_confirmar)) },
            singleLine = true,
            isError = uiState.value.confirmPasswordError != null,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        if (uiState.value.confirmPasswordError != null) {
            Text(
                text = uiState.value.confirmPasswordError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Botón de cambiar contraseña
        Button(
            onClick = { viewModel.changePassword() },
            enabled = uiState.value.passwordError == null && 
                     uiState.value.confirmPasswordError == null && 
                     !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.small,
            elevation = ButtonDefaults.buttonElevation(4.dp)
        ) {
            Text(
                text = stringResource(R.string.cambiar_contrasena_boton),
                style = MaterialTheme.typography.labelSmall
            )
        }

        if (uiState.value.errorMessage != null) {
            Text(
                text = uiState.value.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun S_07_CambiarContrasenaPreview() {
    FerretoolsTheme {
        val navController = rememberNavController()
        S_07_CambiarContrasena(navController = navController)
    }
}