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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.R
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.theme.FerretoolsTheme
import com.example.ferretools.viewmodel.session.RegistroUsuarioViewModel

@Composable
fun S_03_RegistroUsuario(
    navController: NavController,
    rolUsuario: RolUsuario,
    isLoading: Boolean = false,
    registroUsuarioViewModel: RegistroUsuarioViewModel = viewModel()
) {
    val registroUsuarioUiState = registroUsuarioViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val emailFocusRequester = remember { FocusRequester() }
    val phoneFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }

    // Define un launcher para elegir fotos
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        registroUsuarioViewModel.updateUri(uri)
    }

    // Define el rol de usuario en el uiState por única vez
    LaunchedEffect(rolUsuario) {
        registroUsuarioViewModel.setRol(rolUsuario)
    }

    // Cambia de pantalla cuando se confirma que el registro es exitoso
    LaunchedEffect(registroUsuarioUiState.value.registerSuccessful) {
        /*
        when (registroUsuarioUiState.value.rolUsuario) {
            RolUsuario.ADMIN -> {
                if (registroUsuarioUiState.value.isFormValid) {
                    navController.navigate(
                        //AppRoutes.Auth.SELECT_BUSINESS(RolUsuario.ADMIN)
                        AppRoutes.Auth.REGISTER_BUSINESS
                    )
                }
            }
            RolUsuario.CLIENTE -> {
                if (registroUsuarioUiState.value.isFormValid) {
                    navController.navigate(
                        //AppRoutes.Auth.SELECT_BUSINESS(RolUsuario.CLIENTE)
                        AppRoutes.Client.DASHBOARD
                    )
                }
            }
            RolUsuario.ALMACENERO -> {
                if (registroUsuarioUiState.value.isFormValid) {
                    navController.navigate(
                        //AppRoutes.Auth.SELECT_BUSINESS(RolUsuario.ALMACENERO)
                        AppRoutes.Client.DASHBOARD

                        // te manda a cliente porque la uistate se mantiene como empleado a pesar
                        // de no haber aceptado la solicitud de empleado.
                    )
                }
            }
        }
         */
        if (registroUsuarioUiState.value.registerSuccessful) {
            // Todos los usuarios van a seleccionar negocio después del registro
            navController.navigate(AppRoutes.Auth.SELECT_BUSINESS(rolUsuario))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
                    contentDescription = stringResource(R.string.registro_volver),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.registro_crear_cuenta),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

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
            if (registroUsuarioUiState.value.imageUri != null) {
                val painter = rememberAsyncImagePainter(
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(data = registroUsuarioUiState.value.imageUri)
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
                    contentDescription = stringResource(R.string.registro_imagen_perfil),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        FormField(
            label = stringResource(R.string.registro_nombres_completos),
            value = registroUsuarioUiState.value.name,
            onValueChange = { registroUsuarioViewModel.updateName(it) },
            placeholder = stringResource(R.string.registro_ingrese_nombres)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormField(
            label = stringResource(R.string.registro_correo_electronico),
            value = registroUsuarioUiState.value.email,
            onValueChange = { registroUsuarioViewModel.updateEmail(it) },
            placeholder = stringResource(R.string.registro_correo),
            isError = registroUsuarioUiState.value.email.isNotBlank() &&
                    registroUsuarioUiState.value.emailError != null,
            errorText = registroUsuarioUiState.value.emailError,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            modifier = Modifier.focusRequester(emailFocusRequester),
            onNext = { phoneFocusRequester.requestFocus() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormField(
            label = stringResource(R.string.registro_telefono),
            value = registroUsuarioUiState.value.phone,
            onValueChange = { registroUsuarioViewModel.updatePhone(it) },
            placeholder = stringResource(R.string.registro_telefono),
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
            modifier = Modifier.focusRequester(phoneFocusRequester),
            onNext = { passwordFocusRequester.requestFocus() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormField(
            label = stringResource(R.string.registro_contrasena),
            value = registroUsuarioUiState.value.password,
            onValueChange = { registroUsuarioViewModel.updatePassword(it) },
            placeholder = stringResource(R.string.registro_contrasena),
            isPassword = true,
            showPassword = registroUsuarioUiState.value.showPassword,
            onTogglePassword = { registroUsuarioViewModel.toggleShowPassword() },
            isError = registroUsuarioUiState.value.password.isNotBlank() &&
                    registroUsuarioUiState.value.passwordError != null,
            errorText = registroUsuarioUiState.value.passwordError,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            modifier = Modifier.focusRequester(passwordFocusRequester),
            onNext = { confirmPasswordFocusRequester.requestFocus() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormField(
            label = stringResource(R.string.registro_confirmar_contrasena),
            value = registroUsuarioUiState.value.confirmPassword,
            onValueChange = { registroUsuarioViewModel.updateConfirmPassword(it) },
            placeholder = stringResource(R.string.registro_repite_contrasena),
            isPassword = true,
            showPassword = registroUsuarioUiState.value.showConfirmPassword,
            onTogglePassword = { registroUsuarioViewModel.toggleShowConfirmPassword() },
            isError = registroUsuarioUiState.value.confirmPassword.isNotBlank() &&
                    registroUsuarioUiState.value.confirmPasswordError != null,
            errorText = registroUsuarioUiState.value.confirmPasswordError,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            modifier = Modifier.focusRequester(confirmPasswordFocusRequester),
            onNext = { focusManager.clearFocus() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Registrar usuario
                registroUsuarioViewModel.registerUser()
            },
            enabled = registroUsuarioUiState.value.isFormValid && !isLoading,
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
            Text(text = stringResource(R.string.registro_registrarse), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
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
            trailingIcon = if (isPassword && onTogglePassword != null) {
                {
                    val icon = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = onTogglePassword) {
                        Icon(imageVector = icon, contentDescription = if (showPassword) stringResource(R.string.registro_ocultar_contrasena) else stringResource(R.string.registro_mostrar_contrasena))
                    }
                }
            } else null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onNext = { onNext?.invoke() },
                onDone = { onNext?.invoke() }
            ),
            modifier = modifier.fillMaxWidth()
        )
        if (isError && errorText != null) {
//            Log.e("DEBUG", errorText)
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun S_03_RegistroUsuarioPreview() {
    FerretoolsTheme {
        val navController = rememberNavController()
        S_03_RegistroUsuario(navController = navController, rolUsuario = RolUsuario.ADMIN)
    }
}