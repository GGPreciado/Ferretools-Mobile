package com.example.ferretools.ui.session

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.theme.FerretoolsTheme
import com.example.ferretools.viewmodel.session.IniciarSesionViewModel


@Composable
fun S_05_IniciarSesion(
    navController: NavController,
    isLoading: Boolean = false,
    errorMessage: String? = null,
   iniciarSesionViewModel: IniciarSesionViewModel = viewModel()
) {
    val iniciarSesionUiState = iniciarSesionViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        LoginFormField(
            label = "Correo electrónico",
            value = iniciarSesionUiState.value.email,
            onValueChange = { iniciarSesionViewModel.updateEmail(it) },
            placeholder = "Correo",
            keyboardType = KeyboardType.Email,
            isError = iniciarSesionUiState.value.email.isNotBlank() &&
                    iniciarSesionUiState.value.emailError != null,
            errorText = iniciarSesionUiState.value.emailError
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginFormField(
            label = "Contraseña",
            value = iniciarSesionUiState.value.password,
            onValueChange = { iniciarSesionViewModel.updatePassword(it) },
            placeholder = "Contraseña",
            isPassword = true,
            showPassword = iniciarSesionUiState.value.showPassword,
            onTogglePassword = { iniciarSesionViewModel.toggleShowPassword() },
            keyboardType = KeyboardType.Password,
            isError = iniciarSesionUiState.value.password.isNotBlank() &&
                    iniciarSesionUiState.value.passwordError != null,
            errorText = iniciarSesionUiState.value.passwordError
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ForgotPasswordLink(onClick = { navController.navigate(AppRoutes.Auth.RECOVER_PASSWORD) })
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(24.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onError,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        LoginButton(iniciarSesionUiState.value.isFormValid && !isLoading) {
            // Aquí puedes llamar a tu ViewModel o lógica de login
            // viewModel.login(email, password)
            // Por ahora, navega a la pantalla principal del admin (puedes cambiarlo según el rol)
            navController.navigate(AppRoutes.Admin.DASHBOARD)
        }

        Spacer(modifier = Modifier.height(16.dp))
        RegisterLink(onClick = { navController.navigate(AppRoutes.Auth.SELECT_ROLE) })
    }
}

@Composable
fun LoginFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
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
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = if (isPassword && onTogglePassword != null) {
                {
                    val icon = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = onTogglePassword) {
                        Icon(imageVector = icon, contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña")
                    }
                }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
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
fun ForgotPasswordLink(onClick: () -> Unit) {
    Text(
        text = "¿Olvidaste tu contraseña?",
        color = MaterialTheme.colorScheme.tertiary,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.End,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        textDecoration = TextDecoration.Underline
    )
}

@Composable
fun LoginButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
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
        Text(
            text = "INICIAR SESIÓN",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun RegisterLink(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¿No tienes cuenta? ",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Registrarse",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable { onClick() },
            textDecoration = TextDecoration.Underline
        )
    }
}

@Preview(showBackground = true)
@Composable
fun S_05_IniciarSesionPreview() {
    FerretoolsTheme {
        val navController = rememberNavController()
        S_05_IniciarSesion(navController = navController)
    }

}