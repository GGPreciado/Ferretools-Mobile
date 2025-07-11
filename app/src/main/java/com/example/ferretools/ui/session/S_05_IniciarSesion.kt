package com.example.ferretools.ui.session

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.theme.FerretoolsTheme
import com.example.ferretools.utils.SesionUsuario
import com.example.ferretools.viewmodel.session.IniciarSesionViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore
import com.example.ferretools.utils.NotificationHelper
import androidx.compose.ui.res.stringResource
import com.example.ferretools.R


@Composable
fun S_05_IniciarSesion(
    navController: NavController,
    isLoading: Boolean = false,
    iniciarSesionViewModel: IniciarSesionViewModel = viewModel()
) {
    val iniciarSesionUiState = iniciarSesionViewModel.uiState.collectAsState()

    LaunchedEffect(iniciarSesionUiState.value.loginSuccessful) {
        if (iniciarSesionUiState.value.loginSuccessful) {
            // Guardar el token FCM en Firestore al iniciar sesión
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    SesionUsuario.usuario?.let { user ->
                        val db = FirebaseFirestore.getInstance()
                        db.collection("usuarios").document(user.uid)
                            .update("fcmToken", token)
                    }
                }
            }
            // Listener de notificaciones locales SOLO para admin y SOLO una vez por sesión
            if (SesionUsuario.usuario?.rol == RolUsuario.ADMIN) {
                val context = navController.context
                val db = FirebaseFirestore.getInstance()
                val notificationHelper = NotificationHelper(context)
                val negocioId = SesionUsuario.usuario?.negocioId
                db.collection("solicitudes")
                    .whereEqualTo("estado", "pendiente")
                    .whereEqualTo("negocioId", negocioId)
                    .addSnapshotListener { snapshot, _ ->
                        val count = snapshot?.size() ?: 0
                        if (count > 0) {
                            notificationHelper.mostrarNotificacionSolicitud(
                                "Nueva Solicitud de Empleo",
                                "Tienes $count solicitud(es) pendiente(s) de revisión"
                            )
                        }
                    }
            }
        }
        when (SesionUsuario.usuario?.rol) {
            RolUsuario.ADMIN -> {
                navController.navigate(AppRoutes.Admin.DASHBOARD)
            }
            RolUsuario.CLIENTE -> {
                navController.navigate(AppRoutes.Client.DASHBOARD)
            }
            RolUsuario.ALMACENERO -> {
                navController.navigate(AppRoutes.Employee.DASHBOARD)
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.iniciar_sesion_titulo),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        LoginFormField(
            label = stringResource(R.string.iniciar_sesion_correo_electronico),
            value = iniciarSesionUiState.value.email,
            onValueChange = { iniciarSesionViewModel.updateEmail(it) },
            placeholder = stringResource(R.string.iniciar_sesion_correo),
            keyboardType = KeyboardType.Email,
            isError = iniciarSesionUiState.value.email.isNotBlank() &&
                    iniciarSesionUiState.value.emailError != null,
            errorText = iniciarSesionUiState.value.emailError
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginFormField(
            label = stringResource(R.string.iniciar_sesion_contrasena),
            value = iniciarSesionUiState.value.password,
            onValueChange = { iniciarSesionViewModel.updatePassword(it) },
            placeholder = stringResource(R.string.iniciar_sesion_contrasena),
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

        LoginButton(iniciarSesionUiState.value.isFormValid && !isLoading) {
            // Iniciar sesión
            iniciarSesionViewModel.loginUser()
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
                        Icon(imageVector = icon, contentDescription = if (showPassword) stringResource(R.string.iniciar_sesion_ocultar_contrasena) else stringResource(R.string.iniciar_sesion_mostrar_contrasena))
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
        text = stringResource(R.string.iniciar_sesion_olvidaste_contrasena),
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
            text = stringResource(R.string.iniciar_sesion_boton),
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
            text = stringResource(R.string.iniciar_sesion_no_tienes_cuenta),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(R.string.iniciar_sesion_registrarse),
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