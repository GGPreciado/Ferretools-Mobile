package com.example.ferretools.utils

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ferretools.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.firestore.FirebaseFirestore

class FCMService : FirebaseMessagingService() {
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        val user = SesionUsuario.usuario
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios").document(user.uid)
                .update("fcmToken", token)
                .addOnSuccessListener { Log.d("FCM", "Token FCM guardado en Firestore") }
                .addOnFailureListener { e -> Log.e("FCM", "Error guardando token FCM: ${e.message}") }
        } else {
            Log.w("FCM", "No hay usuario en sesión para guardar el token FCM")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d("FCM", "Mensaje recibido: ${remoteMessage.data}")
        
        // Manejar notificación de solicitud
        if (remoteMessage.data["tipo"] == "solicitud_empleo") {
            val titulo = remoteMessage.data["titulo"] ?: "Nueva Solicitud"
            val mensaje = remoteMessage.data["mensaje"] ?: "Un cliente quiere ser empleado"
            mostrarNotificacionSolicitud(titulo, mensaje)
        }
    }

    private fun mostrarNotificacionSolicitud(titulo: String, mensaje: String) {
        val notificationHelper = NotificationHelper(this)
        notificationHelper.mostrarNotificacionSolicitud(titulo, mensaje)
    }
} 