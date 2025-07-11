package com.example.ferretools.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(nombreUsuario, color = Color.Black, fontWeight = FontWeight.Bold)
            Text(nombreTienda, color = Color.Black, fontSize = 13.sp)
        }
    }
}