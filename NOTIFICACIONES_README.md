# Configuración de Notificaciones - Ferretools

## 📱 Notificaciones Implementadas

### 1. Notificaciones Push (FCM) - PRINCIPAL
- **Funcionalidad:** Notificaciones en tiempo real cuando se crea una solicitud
- **Ventaja:** Funciona incluso si la app está cerrada
- **Estado:** ✅ Implementado

### 2. Notificaciones Locales - RESPALDO
- **Funcionalidad:** Notificaciones cuando el admin abre la app
- **Ventaja:** No requiere configuración externa
- **Estado:** ✅ Implementado

---

## 🔧 Configuración Requerida

### Paso 1: Firebase Cloud Messaging
1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto
3. Ve a **Project Settings** > **Cloud Messaging**
4. Descarga el archivo `google-services.json` y colócalo en `app/`
5. Habilita **Cloud Messaging API**

### Paso 2: Dependencias (ya incluidas)
```gradle
// En app/build.gradle.kts
implementation("com.google.firebase:firebase-messaging-ktx:23.4.0")
implementation("androidx.core:core-ktx:1.12.0")
```

### Paso 3: Cloud Functions (Opcional)
1. Instala Firebase CLI: `npm install -g firebase-tools`
2. Inicializa Cloud Functions: `firebase init functions`
3. Copia el código de `cloud-functions/index.js`
4. Despliega: `firebase deploy --only functions`

---

## 🚀 Cómo Funciona

### Flujo de Notificaciones Push:
1. Cliente crea solicitud → Firestore
2. Cloud Function detecta cambio → Envía notificación push
3. Admin recibe notificación en la barra (mientras usa otra app)
4. Al tocar la notificación → Se abre la app en la pantalla de Solicitudes

### Flujo de Notificaciones Locales:
1. Admin abre la app → Se cargan las solicitudes
2. Si hay solicitudes pendientes → Se muestra notificación local
3. Al tocar la notificación → Se navega a la pantalla de Solicitudes

---

## 📋 Archivos Creados/Modificados

### Nuevos Archivos:
- `NotificationHelper.kt` - Helper para notificaciones
- `FCMService.kt` - Servicio de Firebase Cloud Messaging
- `cloud-functions/index.js` - Cloud Function para notificaciones push

### Archivos Modificados:
- `AndroidManifest.xml` - Permisos y servicio FCM
- `RevisarSolicitudesViewModel.kt` - Notificaciones locales
- `MiSolicitudViewModel.kt` - Trigger para notificaciones push
- `SolicitudesScreen.kt` - Inicialización del contexto

---

## 🧪 Pruebas

### Probar Notificaciones Locales:
1. Ejecuta la app
2. Crea una solicitud como cliente
3. Abre la app como admin
4. Deberías ver una notificación local

### Probar Notificaciones Push:
1. Configura Cloud Functions
2. Crea una solicitud como cliente
3. Como admin, cierra la app
4. Deberías recibir notificación push

---

## 🔍 Troubleshooting

### Notificaciones no aparecen:
1. Verifica permisos en Android Settings
2. Asegúrate de que el canal de notificación esté creado
3. Revisa logs de Firebase Console

### Cloud Functions no funcionan:
1. Verifica que estén desplegadas: `firebase functions:list`
2. Revisa logs: `firebase functions:log`
3. Asegúrate de que la API esté habilitada

---

## 📝 Notas Importantes

- Las notificaciones locales funcionan inmediatamente
- Las notificaciones push requieren configuración de Firebase
- Cloud Functions son opcionales pero recomendadas para producción
- Los tokens FCM se generan automáticamente al instalar la app 