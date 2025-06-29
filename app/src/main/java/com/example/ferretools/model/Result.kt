package com.example.ferretools.model

// Clase sellada para representar el resultado de una operación (éxito o error)
// Esta clase es común para todos los ViewModels y Repositorios
sealed class Result<out T> {
    data class Success<T>(val data: T): Result<T>() // Éxito con datos
    data class Error(val message: String): Result<Nothing>() // Error con mensaje
} 