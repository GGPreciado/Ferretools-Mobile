# 📊 Sistema de Reportes de Inventario - Ferretools

## 🎯 Funcionalidades Implementadas

### ✅ **Generación de PDF**
- Reportes profesionales con formato tabular
- Incluye estadísticas del inventario
- Diseño limpio y profesional
- Pie de página con información de la empresa

### ✅ **Generación de Excel**
- Hojas de cálculo con formato profesional
- Headers con estilos y colores
- Autoajuste de columnas
- Incluye estadísticas y datos completos

### ✅ **Compartir Reportes**
- Generación de contenido formateado
- Compatible con todas las apps de compartir
- Incluye emojis y formato legible
- Estadísticas resumidas

## 🚀 Cómo Usar

### **1. Acceder al Reporte**
1. Ve a **Inventario** → **Reportes**
2. O desde la pantalla de lista de productos, toca el botón de **Reportes**

### **2. Filtrar Productos**
- Usa los chips de categorías para filtrar productos
- Selecciona "Todas las categorías" para ver todo el inventario

### **3. Generar Reportes**
Toca el botón de **tres puntos** (⋮) y selecciona:

#### **📄 Descargar PDF**
- Genera un archivo PDF profesional
- Se guarda en la carpeta de la app
- Incluye tabla con todos los productos
- Estadísticas completas del inventario

#### **📊 Descargar Excel**
- Genera una hoja de cálculo Excel (.xlsx)
- Formato profesional con estilos
- Datos organizados en columnas
- Autoajuste automático

#### **📤 Compartir**
- Genera contenido formateado
- Se abre el selector de apps para compartir
- Compatible con WhatsApp, Email, etc.
- Incluye estadísticas resumidas

## 📋 Contenido de los Reportes

### **Estadísticas Incluidas:**
- 📦 Total de productos
- 💰 Valor total del inventario
- ⚠️ Productos con bajo stock (< 10 unidades)
- 📅 Fecha y hora de generación

### **Datos de Productos:**
- Nombre del producto
- Precio unitario
- Stock disponible
- Valor total (precio × stock)
- Categoría (en Excel)

## 🔧 Configuración Técnica

### **Dependencias Agregadas:**
```kotlin
// PDF Generation
implementation("com.itextpdf:itext7-core:7.2.3")

// Excel Generation  
implementation("org.apache.poi:poi:5.2.3")
implementation("org.apache.poi:poi-ooxml:5.2.3")
```

### **Permisos Requeridos:**
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
    android:maxSdkVersion="28" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" 
    tools:ignore="ScopedStorage" />
```

## 📁 Ubicación de Archivos

Los archivos generados se guardan en:
```
/storage/emulated/0/Android/data/com.example.ferretools/files/
```

### **Nomenclatura de Archivos:**
- PDF: `reporte_inventario_[timestamp].pdf`
- Excel: `reporte_inventario_[timestamp].xlsx`

## 🎨 Características de UX

### **Indicadores de Carga:**
- Spinner durante la generación
- Botones deshabilitados durante el proceso
- Feedback visual inmediato

### **Manejo de Errores:**
- Diálogos informativos de éxito/error
- Logs detallados para debugging
- Recuperación graceful de errores

### **Accesibilidad:**
- Iconos descriptivos
- Textos claros y concisos
- Navegación intuitiva

## 🔄 Flujo de Trabajo

1. **Usuario accede** a la pantalla de reportes
2. **Filtra productos** por categoría (opcional)
3. **Selecciona formato** (PDF/Excel/Compartir)
4. **Sistema genera** el reporte en segundo plano
5. **Archivo se guarda** o contenido se comparte
6. **Feedback** se muestra al usuario

## 🛠️ Mantenimiento

### **Personalización de Reportes:**
Para modificar el formato de los reportes, edita:
- `ReportGenerator.kt` - Lógica de generación
- `ReporteInventarioViewModel.kt` - Manejo de estado

### **Agregar Nuevos Formatos:**
1. Agregar dependencia en `build.gradle.kts`
2. Implementar función en `ReportGenerator.kt`
3. Agregar opción en el modal de la UI
4. Actualizar ViewModel

## 📈 Próximas Mejoras

- [ ] **Filtros avanzados** (por precio, stock, etc.)
- [ ] **Gráficos en reportes** (barras, pastel)
- [ ] **Reportes programados** (automáticos)
- [ ] **Plantillas personalizables**
- [ ] **Exportación a Google Drive**
- [ ] **Reportes comparativos** (mes a mes)

## 🐛 Solución de Problemas

### **Error al generar PDF:**
- Verificar permisos de almacenamiento
- Comprobar espacio disponible
- Revisar logs de error

### **Error al generar Excel:**
- Verificar dependencias de Apache POI
- Comprobar formato de datos
- Revisar memoria disponible

### **Archivo no se guarda:**
- Verificar permisos de escritura
- Comprobar espacio en almacenamiento
- Revisar configuración de archivos

---

**Desarrollado para Ferretools - Sistema de Gestión de Inventario**  
**Versión:** 1.0  
**Fecha:** ${new Date().toLocaleDateString()} 