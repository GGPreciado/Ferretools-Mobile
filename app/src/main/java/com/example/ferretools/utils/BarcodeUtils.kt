package com.example.ferretools.utils

import android.util.Log

object BarcodeUtils {
    
    /**
     * Valida si un código de barras tiene un formato válido
     */
    fun isValidBarcode(barcode: String): Boolean {
        return barcode.isNotBlank() && barcode.length >= 8
    }
    
    /**
     * Limpia el código de barras de caracteres especiales
     */
    fun cleanBarcode(barcode: String): String {
        return barcode.trim().replace(Regex("[^0-9]"), "")
    }
    
    /**
     * Determina el tipo de código de barras basado en su longitud
     */
    fun getBarcodeType(barcode: String): BarcodeType {
        return when (barcode.length) {
            8 -> BarcodeType.EAN_8
            12 -> BarcodeType.UPC_A
            13 -> BarcodeType.EAN_13
            14 -> BarcodeType.EAN_14
            else -> BarcodeType.UNKNOWN
        }
    }
    
    /**
     * Logs para debugging
     */
    fun logBarcodeScan(barcode: String, productFound: Boolean, productName: String? = null) {
        Log.d("BarcodeUtils", "Código escaneado: $barcode")
        Log.d("BarcodeUtils", "Producto encontrado: $productFound")
        if (productFound && productName != null) {
            Log.d("BarcodeUtils", "Nombre del producto: $productName")
        }
    }
}

enum class BarcodeType {
    EAN_8,
    EAN_13,
    EAN_14,
    UPC_A,
    CODE_128,
    CODE_39,
    UNKNOWN
} 