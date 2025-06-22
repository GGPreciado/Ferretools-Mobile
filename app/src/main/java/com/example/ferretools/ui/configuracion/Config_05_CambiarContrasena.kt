package com.example.ferretools.ui.configuracion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.viewmodel.session.CambiarContrasenaViewModel

@Composable
fun Config_05_CambiarContrasena(
    navController: NavController,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    viewModel: CambiarContrasenaViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    // Navegar de vuelta cuando la contraseña se haya cambiado exitosamente
    LaunchedEffect(uiState.value.passwordChanged) {
        if (uiState.value.passwordChanged) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar { navController.popBackStack() }

        TitleText("Cambiar Contraseña")

        // Campo de contraseña actual
        PasswordSettingsFormField(
            label = "Contraseña actual",
            value = uiState.value.currentPassword,
            onValueChange = { viewModel.updateCurrentPassword(it) },
            placeholder = "Contraseña actual",
            showPassword = false,
            onTogglePassword = { },
            isError = uiState.value.currentPasswordError != null,
            errorText = uiState.value.currentPasswordError
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Campo de nueva contraseña
        PasswordSettingsFormField(
            label = "Nueva contraseña",
            value = uiState.value.password,
            onValueChange = { viewModel.updatePassword(it) },
            placeholder = "Nueva contraseña",
            showPassword = false,
            onTogglePassword = { },
            isError = uiState.value.passwordError != null,
            errorText = uiState.value.passwordError
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Campo de confirmar nueva contraseña
        PasswordSettingsFormField(
            label = "Confirmar nueva contraseña",
            value = uiState.value.confirmPassword,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            placeholder = "Repetir nueva contraseña",
            showPassword = false,
            onTogglePassword = { },
            isError = uiState.value.confirmPasswordError != null,
            errorText = uiState.value.confirmPasswordError
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar mensaje de error del ViewModel o del parámetro
        (uiState.value.errorMessage ?: errorMessage)?.let {
            MessageText(it, color = Color.Red)
        }

        Button(
            onClick = { viewModel.changePassword() },
            enabled = uiState.value.currentPasswordError == null &&
                     uiState.value.passwordError == null && 
                     uiState.value.confirmPasswordError == null && 
                     !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(2.dp)
        ) {
            Text("GUARDAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color(0xFF333333)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun TitleText(text: String) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun MessageText(text: String, color: Color, bold: Boolean = false) {
    Text(
        text = text,
        color = color,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun PasswordSettingsFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    showPassword: Boolean,
    onTogglePassword: () -> Unit,
    isError: Boolean = false,
    errorText: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            singleLine = true,
            isError = isError,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewConfig_05_CambiarContrasena() {
    val navController = rememberNavController()
    Config_05_CambiarContrasena(navController = navController)
}