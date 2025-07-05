package com.example.ferretools.ui.components.seleccion_productos

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ferretools.R

@Composable
fun ScanButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(R.drawable.escaner),
        contentDescription = "Escanear producto",
        modifier = modifier
            .size(45.dp)
            .clickable { onClick() }
    )
}