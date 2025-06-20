package com.example.ferretools

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.ferretools.model.database.Usuario
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.navigation.AppNavigation
import com.example.ferretools.theme.FerretoolsTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.example.ferretools.utils.NotificationHelper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    /*
    // --- LISTENER GLOBAL DE NOTIFICACIONES DE SOLICITUDES ---
    // Puedes comentar este bloque para desactivar las notificaciones locales globales
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val notificationHelper = NotificationHelper(context)
        db.collection("solicitudes")
            .whereEqualTo("estado", "pendiente")
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
    */

    FerretoolsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            //NavGraph(navController = navController)
            //MainAppNavigation(navController = navController)
            AppNavigation(navController = navController)
        }
    }
}

