package com.example.ferretools.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.ferretools.utils.SesionUsuario

@Composable
fun UserDataBar(
    nombreUsuario: String,
    nombreTienda: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF00BF59))
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .padding(top = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen de usuario (placeholder)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        ) {
            ProfileImage(
                imageRemoteUrl = SesionUsuario.usuario?.fotoUrl,
                imageLocalUri = null
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(nombreUsuario, color = Color.Black, fontWeight = FontWeight.Bold)
            Text(nombreTienda, color = Color.Black, fontSize = 13.sp)
        }
    }
}

@Composable
fun ProfileImage(imageRemoteUrl: String?, imageLocalUri: Uri?) {
    val context = LocalContext.current
    val imageToDisplay: Any? = imageLocalUri ?: imageRemoteUrl

    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceDim),
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