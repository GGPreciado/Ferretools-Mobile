package com.example.ferretools.utils

import android.content.Context
import android.net.Uri
import com.example.ferretools.model.database.Producto
import com.example.ferretools.viewmodel.balance.BalanceResumen
import com.example.ferretools.viewmodel.balance.Movimiento
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
//import com.itextpdf.layout.property.TextAlignment
//import com.itextpdf.layout.property.UnitValue
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ReportGenerator {
    
    companion object {
        
        /**
         * Genera un reporte PDF del inventario
         */
        fun generarPDF(productos: List<Producto>, fecha: String): ByteArray {
            val outputStream = ByteArrayOutputStream()
            val pdfWriter = PdfWriter(outputStream)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)
            
            try {
                // Título del reporte
                val titulo = Paragraph("📊 REPORTE DE INVENTARIO")
                    .setFontSize(20f)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                document.add(titulo)
                
                // Fecha
                val fechaParrafo = Paragraph("📅 Fecha: $fecha")
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.CENTER)
                document.add(fechaParrafo)
                
                // Estadísticas
                val totalProductos = productos.size
                val valorTotal = productos.sumOf { it.precio * it.cantidad_disponible }
                val productosBajoStock = productos.count { it.cantidad_disponible < 10 }
                
                val estadisticas = Paragraph("""
                    
                    📦 Total de productos: $totalProductos
                    💰 Valor total: S/ ${String.format("%.2f", valorTotal)}
                    ⚠️ Productos con bajo stock: $productosBajoStock
                """.trimIndent())
                    .setFontSize(11f)
                document.add(estadisticas)
                
                // Tabla de productos
                if (productos.isNotEmpty()) {
                    val tabla = Table(UnitValue.createPercentArray(floatArrayOf(40f, 20f, 20f, 20f)))
                        .useAllAvailableWidth()
                    
                    // Headers de la tabla
                    val headers = arrayOf("Producto", "Precio (S/)", "Stock", "Valor Total")
                    headers.forEach { header ->
                        val celda = Paragraph(header).setBold()
                        tabla.addCell(celda)
                    }
                    
                    // Datos de productos
                    productos.forEach { producto ->
                        tabla.addCell(Paragraph(producto.nombre))
                        tabla.addCell(Paragraph(String.format("%.2f", producto.precio)))
                        tabla.addCell(Paragraph(producto.cantidad_disponible.toString()))
                        tabla.addCell(Paragraph(String.format("%.2f", producto.precio * producto.cantidad_disponible)))
                    }
                    
                    document.add(tabla)
                }
                
                // Pie de página
                val piePagina = Paragraph("""
                    
                    
                    Reporte generado automáticamente por Ferretools
                    © ${Calendar.getInstance().get(Calendar.YEAR)} Ferretools - Sistema de Gestión de Inventario
                """.trimIndent())
                    .setFontSize(8f)
                    .setTextAlignment(TextAlignment.CENTER)
                document.add(piePagina)
                
            } finally {
                document.close()
            }
            
            return outputStream.toByteArray()
        }
        
        /**
         * Genera un reporte Excel del inventario
         */
        fun generarExcel(productos: List<Producto>, fecha: String): ByteArray {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Inventario")
            
            // Estilos para headers
            val headerStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
                fillPattern = org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND
                borderBottom = org.apache.poi.ss.usermodel.BorderStyle.THIN
                borderTop = org.apache.poi.ss.usermodel.BorderStyle.THIN
                borderRight = org.apache.poi.ss.usermodel.BorderStyle.THIN
                borderLeft = org.apache.poi.ss.usermodel.BorderStyle.THIN
            }
            
            val headerFont = workbook.createFont().apply {
                bold = true
            }
            headerStyle.setFont(headerFont)
            
            // Estilos para datos
            val dataStyle = workbook.createCellStyle().apply {
                borderBottom = org.apache.poi.ss.usermodel.BorderStyle.THIN
                borderTop = org.apache.poi.ss.usermodel.BorderStyle.THIN
                borderRight = org.apache.poi.ss.usermodel.BorderStyle.THIN
                borderLeft = org.apache.poi.ss.usermodel.BorderStyle.THIN
            }
            
            var rowNum = 0
            
            // Título del reporte
            val tituloRow = sheet.createRow(rowNum++)
            val tituloCell = tituloRow.createCell(0)
            tituloCell.setCellValue("📊 REPORTE DE INVENTARIO")
            tituloCell.cellStyle = headerStyle
            
            // Fecha
            val fechaRow = sheet.createRow(rowNum++)
            val fechaCell = fechaRow.createCell(0)
            fechaCell.setCellValue("📅 Fecha: $fecha")
            
            // Línea en blanco
            rowNum++
            
            // Estadísticas
            val totalProductos = productos.size
            val valorTotal = productos.sumOf { it.precio * it.cantidad_disponible }
            val productosBajoStock = productos.count { it.cantidad_disponible < 10 }
            
            val statsRow1 = sheet.createRow(rowNum++)
            statsRow1.createCell(0).setCellValue("📦 Total de productos: $totalProductos")
            
            val statsRow2 = sheet.createRow(rowNum++)
            statsRow2.createCell(0).setCellValue("💰 Valor total: S/ ${String.format("%.2f", valorTotal)}")
            
            val statsRow3 = sheet.createRow(rowNum++)
            statsRow3.createCell(0).setCellValue("⚠️ Productos con bajo stock: $productosBajoStock")
            
            // Línea en blanco
            rowNum++
            
            // Headers de la tabla
            val headerRow = sheet.createRow(rowNum++)
            val headers = arrayOf("Producto", "Precio (S/)", "Stock", "Valor Total", "Categoría")
            
            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
                cell.cellStyle = headerStyle
            }
            
            // Datos de productos
            productos.forEach { producto ->
                val dataRow = sheet.createRow(rowNum++)
                
                dataRow.createCell(0).apply {
                    setCellValue(producto.nombre)
                    cellStyle = dataStyle
                }
                
                dataRow.createCell(1).apply {
                    setCellValue(producto.precio)
                    cellStyle = dataStyle
                }
                
                dataRow.createCell(2).apply {
                    setCellValue(producto.cantidad_disponible.toDouble())
                    cellStyle = dataStyle
                }
                
                dataRow.createCell(3).apply {
                    setCellValue(producto.precio * producto.cantidad_disponible)
                    cellStyle = dataStyle
                }
                
                dataRow.createCell(4).apply {
                    setCellValue(producto.categoria_id)
                    cellStyle = dataStyle
                }
            }
            
            // Autoajustar columnas
            for (i in 0 until headers.size) {
                sheet.autoSizeColumn(i)
            }
            
            // Generar el archivo
            val outputStream = ByteArrayOutputStream()
            workbook.write(outputStream)
            workbook.close()
            
            return outputStream.toByteArray()
        }
        
        /**
         * Guarda un archivo en el almacenamiento del dispositivo
         */
        fun guardarArchivo(context: Context, contenido: ByteArray, nombreArchivo: String, mimeType: String): Uri? {
            return try {
                val file = File(context.getExternalFilesDir(null), nombreArchivo)
                FileOutputStream(file).use { fos ->
                    fos.write(contenido)
                }
                Uri.fromFile(file)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        
        /**
         * Genera un reporte PDF del balance
         */
        fun generarPDFBalance(
            resumen: BalanceResumen, 
            movimientos: List<Movimiento>, 
            fecha: String,
            negocioNombre: String
        ): ByteArray {
            val outputStream = ByteArrayOutputStream()
            val pdfWriter = PdfWriter(outputStream)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)
            
            try {
                // Título del reporte
                val titulo = Paragraph("💰 REPORTE DE BALANCE")
                    .setFontSize(20f)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                document.add(titulo)
                
                // Información del negocio
                val negocioParrafo = Paragraph("🏪 Negocio: $negocioNombre")
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.CENTER)
                document.add(negocioParrafo)
                
                // Fecha
                val fechaParrafo = Paragraph("📅 Fecha: $fecha")
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.CENTER)
                document.add(fechaParrafo)
                
                // Resumen del balance
                val resumenTexto = Paragraph("""
                    
                    💰 RESUMEN DEL BALANCE
                    
                    💵 Total: S/ ${String.format("%.2f", resumen.total)}
                    📈 Ingresos: S/ ${String.format("%.2f", resumen.ingresos)}
                    📉 Egresos: S/ ${String.format("%.2f", resumen.egresos)}
                """.trimIndent())
                    .setFontSize(11f)
                document.add(resumenTexto)
                
                // Tabla de movimientos
                if (movimientos.isNotEmpty()) {
                    val tabla = Table(UnitValue.createPercentArray(floatArrayOf(40f, 20f, 20f, 20f)))
                        .useAllAvailableWidth()
                    
                    // Headers de la tabla
                    val headers = arrayOf("Productos", "Fecha", "Monto (S/)", "Método")
                    headers.forEach { header ->
                        val celda = Paragraph(header).setBold()
                        tabla.addCell(celda)
                    }
                    
                    // Datos de movimientos
                    movimientos.forEach { movimiento ->
                        tabla.addCell(Paragraph(movimiento.productos))
                        tabla.addCell(Paragraph(movimiento.fecha))
                        tabla.addCell(Paragraph(String.format("%.2f", movimiento.monto)))
                        tabla.addCell(Paragraph(movimiento.metodo))
                    }
                    
                    document.add(tabla)
                }
                
                // Pie de página
                val piePagina = Paragraph("""
                    
                    
                    Reporte generado automáticamente por Ferretools
                    © ${Calendar.getInstance().get(Calendar.YEAR)} Ferretools - Sistema de Gestión de Inventario
                """.trimIndent())
                    .setFontSize(8f)
                    .setTextAlignment(TextAlignment.CENTER)
                document.add(piePagina)
                
            } finally {
                document.close()
            }
            
            return outputStream.toByteArray()
        }
        
        /**
         * Genera contenido para compartir
         */
        fun generarContenidoCompartir(productos: List<Producto>, fecha: String): String {
            val totalProductos = productos.size
            val valorTotal = productos.sumOf { it.precio * it.cantidad_disponible }
            val productosBajoStock = productos.count { it.cantidad_disponible < 10 }
            
            val contenido = StringBuilder()
            contenido.append("📊 REPORTE DE INVENTARIO\n\n")
            contenido.append("📅 Fecha: $fecha\n")
            contenido.append("📦 Total de productos: $totalProductos\n")
            contenido.append("💰 Valor total: S/ ${String.format("%.2f", valorTotal)}\n")
            contenido.append("⚠️ Productos con bajo stock: $productosBajoStock\n\n")
            contenido.append("📋 PRODUCTOS:\n")
            
            productos.forEach { producto ->
                contenido.append("• ${producto.nombre} - S/ ${String.format("%.2f", producto.precio)} - Stock: ${producto.cantidad_disponible}\n")
            }
            
            contenido.append("\n---\n")
            contenido.append("Reporte generado por Ferretools")
            
            return contenido.toString()
        }
    }
} 