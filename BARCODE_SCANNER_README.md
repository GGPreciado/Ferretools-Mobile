# Escáner de Códigos de Barras - Ferretools

## Descripción
Esta funcionalidad permite escanear códigos de barras de productos directamente desde la aplicación para agregarlos al carrito de ventas de forma rápida y eficiente.

## Características

### ✅ Funcionalidades Implementadas
- **Escáner de cámara en tiempo real** usando CameraX
- **Reconocimiento de códigos de barras** usando ML Kit de Google
- **Soporte para múltiples formatos**:
  - EAN-8
  - EAN-13
  - UPC-A
  - UPC-E
  - Code 128
  - Code 39
- **Validación de permisos** de cámara
- **Búsqueda automática** de productos por código de barras
- **Validación de stock** antes de agregar al carrito
- **Interfaz intuitiva** con marco de escaneo visual
- **Manejo de errores** y mensajes informativos

### 🎯 Flujo de Uso
1. **Acceso**: Desde la pantalla de carrito de ventas (`V_01_CarritoVenta`)
2. **Escaneo**: Toca el botón de escáner (icono de cámara)
3. **Permisos**: Concede permiso de cámara si es la primera vez
4. **Escaneo**: Coloca el código de barras dentro del marco verde
5. **Resultado**: El producto se agrega automáticamente al carrito
6. **Retorno**: Vuelve automáticamente a la pantalla de carrito

## Arquitectura Técnica

### Componentes Principales

#### 1. **BarcodeScannerScreen**
- **Ubicación**: `app/src/main/java/com/example/ferretools/ui/venta/BarcodeScannerScreen.kt`
- **Responsabilidad**: Interfaz de usuario del escáner
- **Tecnologías**: CameraX, ML Kit, Jetpack Compose

#### 2. **VentaViewModel**
- **Método**: `buscarProductoPorCodigoBarras(codigoBarras: String)`
- **Responsabilidad**: Lógica de negocio para búsqueda y validación

#### 3. **BarcodeUtils**
- **Ubicación**: `app/src/main/java/com/example/ferretools/utils/BarcodeUtils.kt`
- **Responsabilidad**: Utilidades para validación y limpieza de códigos

### Dependencias Agregadas

```kotlin
// ML Kit para escáner de códigos de barras
implementation("com.google.mlkit:barcode-scanning:17.2.0")

// CameraX para la cámara
implementation("androidx.camera:camera-core:1.3.1")
implementation("androidx.camera:camera-camera2:1.3.1")
implementation("androidx.camera:camera-lifecycle:1.3.1")
implementation("androidx.camera:camera-view:1.3.1")

// Permisos
implementation("com.google.accompanist:accompanist-permissions:0.32.0")
```

### Permisos Requeridos

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />
```

## Navegación

### Rutas
- **Ruta del escáner**: `AppRoutes.Sale.BARCODE_SCANNER`
- **Navegación**: Desde `V_01_CarritoVenta` → `BarcodeScannerScreen`

### Flujo de Navegación
```
V_01_CarritoVenta (botón escáner) 
    ↓
BarcodeScannerScreen (escaneo)
    ↓
V_01_CarritoVenta (producto agregado)
```

## Validaciones Implementadas

### 1. **Validación de Código de Barras**
- Longitud mínima: 8 caracteres
- Limpieza de caracteres especiales
- Formato numérico

### 2. **Validación de Producto**
- Existencia en la base de datos
- Coincidencia exacta de código de barras

### 3. **Validación de Stock**
- Verificación de stock disponible
- Prevención de agregar más del stock existente

### 4. **Validación de Permisos**
- Solicitud automática de permisos de cámara
- Manejo de denegación de permisos

## Manejo de Errores

### Tipos de Error
1. **Código inválido**: "Código de barras inválido"
2. **Producto no encontrado**: "Producto no encontrado con código: [código]"
3. **Stock insuficiente**: "Stock insuficiente para [nombre_producto]"
4. **Sin permisos**: Pantalla de solicitud de permisos

### Visualización de Errores
- **Banner superior**: Mensajes temporales (2 segundos)
- **Logs**: Para debugging en Logcat

## Optimizaciones

### 1. **Performance**
- Análisis de imagen optimizado (`STRATEGY_KEEP_ONLY_LATEST`)
- Ejecutor dedicado para procesamiento de cámara
- Limpieza automática de recursos

### 2. **UX**
- Marco visual para guiar el escaneo
- Instrucciones claras en pantalla
- Navegación automática tras escaneo exitoso

### 3. **Robustez**
- Manejo de errores de cámara
- Validación de códigos antes de búsqueda
- Logs detallados para debugging

## Pruebas Recomendadas

### 1. **Funcionalidad Básica**
- [ ] Escaneo de códigos EAN-13 válidos
- [ ] Escaneo de códigos EAN-8 válidos
- [ ] Escaneo de códigos UPC-A válidos

### 2. **Casos de Error**
- [ ] Códigos de barras inválidos
- [ ] Productos no existentes en BD
- [ ] Stock insuficiente
- [ ] Denegación de permisos

### 3. **Integración**
- [ ] Agregado correcto al carrito
- [ ] Actualización de total
- [ ] Navegación fluida

## Futuras Mejoras

### 🚀 Posibles Extensiones
1. **Escáner de QR**: Para códigos QR de productos
2. **Historial de escaneos**: Últimos códigos escaneados
3. **Escaneo múltiple**: Agregar varios productos de una vez
4. **Modo offline**: Cache de códigos frecuentes
5. **Sonidos**: Feedback auditivo al escanear
6. **Vibración**: Feedback háptico

### 🔧 Optimizaciones Técnicas
1. **Cache de productos**: Reducir consultas a BD
2. **Compresión de imagen**: Mejorar performance
3. **Modo nocturno**: Para escaneo en baja luz
4. **Zoom automático**: Para códigos pequeños

## Troubleshooting

### Problemas Comunes

#### 1. **Cámara no funciona**
- Verificar permisos en Configuración > Apps > Ferretools > Permisos
- Reiniciar la aplicación

#### 2. **Códigos no se reconocen**
- Verificar que el código esté bien iluminado
- Asegurar que el código esté dentro del marco
- Verificar que el código esté en formato soportado

#### 3. **Productos no se encuentran**
- Verificar que el código de barras esté registrado en la BD
- Revisar logs para debugging

## Contribución

Para agregar nuevas funcionalidades al escáner:

1. **Nuevos formatos**: Agregar en `BarcodeScannerScreen.kt`
2. **Validaciones**: Extender `BarcodeUtils.kt`
3. **UI**: Modificar componentes en `BarcodeScannerScreen.kt`
4. **Lógica**: Actualizar `VentaViewModel.kt`

---

**Desarrollado para Ferretools** - Sistema de Gestión de Ferretería 