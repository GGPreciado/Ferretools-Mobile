package com.example.ferretools.ui.configuracion

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.navigation.AppRoutes
import com.example.ferretools.theme.FerretoolsTheme
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun Config_01_Configuracion(
    navController: NavController,
    darkModeEnabled: Boolean,
    stockNotificationEnabled: Boolean,
    // viewModel: ConfiguracionViewModel = viewModel() // Para uso futuro
) {
    Log.e("DEBUG", "Pantalla Configuración - rol actual: ${SesionUsuario.usuario?.rol}, rol deseado: ${SesionUsuario.rolDeseado}")
    val usuarioActual = SesionUsuario.usuario
    var notificacionSolicitudesEnabled by remember { mutableStateOf(usuarioActual?.rol == RolUsuario.ADMIN && (usuarioActual?.let { it.notificacionSolicitudes } ?: true)) }

    // Leer el valor actualizado de Firestore al entrar a la pantalla
    LaunchedEffect(usuarioActual?.uid) {
        if (usuarioActual?.rol == RolUsuario.ADMIN) {
            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios").document(usuarioActual.uid).get()
                .addOnSuccessListener { doc ->
                    val valor = doc.getBoolean("notificacionSolicitudes") ?: true
                    notificacionSolicitudesEnabled = valor
                    SesionUsuario.actualizarDatos(notificacionSolicitudes = valor)
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Bar
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
            IconButton(onClick = { navController.navigate(AppRoutes.Config.EDIT_PROFILE) }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar perfil",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Información de Usuario
        Text(
            text = SesionUsuario.usuario!!.nombre,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        UserContactInfo(icon = Icons.Default.Phone, value = SesionUsuario.usuario!!.celular)
        UserContactInfo(icon = Icons.Default.Email, value = SesionUsuario.usuario!!.correo)

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Column(modifier = Modifier.fillMaxWidth()) {

            if (SesionUsuario.usuario!!.rol == RolUsuario.ADMIN) {
                SettingsItem(
                    icon = Icons.Default.Warehouse,
                    text = "Editar datos del negocio",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = { navController.navigate(AppRoutes.Config.EDIT_BUSINESS) }
                )
                Spacer(modifier = Modifier.height(20.dp))
                SettingsItem(
                    icon = Icons.Default.PersonAdd,
                    text = "Solicitudes",
                    color = Color(0xFF2563EB),
                    onClick = { navController.navigate(AppRoutes.Config.SOLICITUDES) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (usuarioActual?.rol == RolUsuario.CLIENTE) {
                SettingsItem(
                    icon = Icons.Default.Person,
                    text = "Mi Solicitud",
                    color = Color(0xFF2563EB),
                    onClick = { navController.navigate(AppRoutes.Config.MI_SOLICITUD) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            SettingsItem(
                icon = Icons.Default.QrCode,
                text = "Cambiar QR de Yape",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = { navController.navigate(AppRoutes.Config.CHANGE_QR) }
            )
            Spacer(modifier = Modifier.height(20.dp))

            SettingsSwitchItem(
                icon = Icons.Default.DarkMode,
                text = "Modo oscuro",
                checked = darkModeEnabled,
                onCheckedChange = { /* TODO: Implementar lógica de cambio de modo oscuro */ }
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (usuarioActual?.rol == RolUsuario.ADMIN) {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    text = "Notificación nueva solicitud",
                    checked = notificacionSolicitudesEnabled,
                    onCheckedChange = { checked ->
                        notificacionSolicitudesEnabled = checked
                        usuarioActual?.let { user ->
                            val db = FirebaseFirestore.getInstance()
                            db.collection("usuarios").document(user.uid)
                                .update("notificacionSolicitudes", checked)
                            // Actualizar también en la sesión local
                            SesionUsuario.actualizarDatos(notificacionSolicitudes = checked)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            SettingsItem(
                icon = Icons.Default.Lock,
                text = "Cambiar Contraseña",
                color = MaterialTheme.colorScheme.tertiary,
                onClick = { navController.navigate(AppRoutes.Config.CHANGE_PASSWORD) }
            )
            Spacer(modifier = Modifier.height(20.dp))

            SettingsItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                text = "Cerrar Sesión",
                color = MaterialTheme.colorScheme.error,
                onClick = { navController.navigate(AppRoutes.Config.CONFIRM_LOGOUT) }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )
    }
}

@Composable
fun UserContactInfo(icon: ImageVector, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Config_01_ConfiguracionPreview() {
    FerretoolsTheme {
        val navController = rememberNavController()
        Config_01_Configuracion(
            navController = navController,
            darkModeEnabled = false,
            stockNotificationEnabled = true
        )
    }

}