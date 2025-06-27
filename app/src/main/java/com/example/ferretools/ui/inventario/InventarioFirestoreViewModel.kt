package com.example.ferretools.ui.inventario

import androidx.lifecycle.ViewModel
import com.example.ferretools.model.database.Producto
import com.example.ferretools.utils.SesionUsuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Objeto singleton temporal para compartir el producto seleccionado
object ProductoSeleccionadoManager {
    private var productoSeleccionado: Producto? = null
    
    fun seleccionarProducto(producto: Producto) {
        println("DEBUG: ProductoSeleccionadoManager - Seleccionando: ${producto.nombre}")
        productoSeleccionado = producto
    }
    
    fun obtenerProducto(): Producto? {
        println("DEBUG: ProductoSeleccionadoManager - Obteniendo: ${productoSeleccionado?.nombre}")
        return productoSeleccionado
    }
    
    fun limpiar() {
        println("DEBUG: ProductoSeleccionadoManager - Limpiando")
        productoSeleccionado = null
    }
}

// ViewModel para gestionar productos de inventario directamente con Firestore
class InventarioFirestoreViewModel : ViewModel() {
    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()
    // StateFlow para la lista de productos
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // Estado para el producto seleccionado
    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado

    // Listener para cambios en la colección de productos
    private var listenerRegistration: ListenerRegistration? = null

    init {
        escucharProductos() // Al crear el ViewModel, empieza a escuchar cambios
    }

    // Escucha cambios en la colección "productos" de Firestore
    private fun escucharProductos() {
        val negocioId = SesionUsuario.usuario?.negocioId
        listenerRegistration = db.collection("productos")
            .whereEqualTo("negocio_id", negocioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("ERROR: Error al escuchar productos: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { doc ->
                        val producto = doc.toObject(Producto::class.java)
                        producto?.let {
                            it.copy(producto_id = doc.id) // Asignar el ID del documento
                        }
                    }
                    println("DEBUG: Productos actualizados desde Firestore: ${lista.size} productos")
                    lista.forEach { producto ->
                        println("DEBUG: Producto '${producto.nombre}' - categoria_id: '${producto.categoria_id}'")
                    }
                    _productos.value = lista
                }
            }
    }

    // Cancela el listener cuando el ViewModel se destruye
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    // Agrega un producto a Firestore
    fun agregarProducto(producto: Producto, onResult: (Boolean) -> Unit) {
        println("DEBUG: Intentando agregar producto: ${producto.nombre} con categoria_id: ${producto.categoria_id}")
        
        db.collection("productos")
            .add(producto)
            .addOnSuccessListener { documentReference ->
                println("DEBUG: Producto agregado exitosamente con ID: ${documentReference.id}")

                // Construyo un Producto con el ID asignado
                val nuevoProducto = producto.copy(producto_id = documentReference.id)

                // Actualizar inmediatamente el estado local
                val listaActual = _productos.value.toMutableList()
                listaActual.add(nuevoProducto)
                _productos.value = listaActual
                
                println("DEBUG: Estado local actualizado. Total productos: ${_productos.value.size}")
                onResult(true)
            }
            .addOnFailureListener { exception ->
                println("ERROR: Error al agregar producto: ${exception.message}")
                onResult(false)
            }
    }

    // Función para forzar la recarga de productos
    fun recargarProductos() {
        println("DEBUG: Forzando recarga de productos...")
        escucharProductos()
    }

    // Función para verificar productos de una categoría específica
    fun verificarProductosCategoria(categoriaId: String) {
        println("DEBUG: Verificando productos para categoría: $categoriaId")
        val productosCategoria = _productos.value.filter { it.categoria_id == categoriaId }
        println("DEBUG: Productos encontrados para categoría $categoriaId: ${productosCategoria.size}")
        productosCategoria.forEach { producto ->
            println("DEBUG: - ${producto.nombre}")
        }
    }

    // Función para obtener productos de una categoría específica
    fun obtenerProductosCategoria(categoriaId: String): List<Producto> {
        return _productos.value.filter { it.categoria_id == categoriaId }
    }

    // Función para seleccionar un producto
    fun seleccionarProducto(producto: Producto) {
        println("DEBUG: Producto seleccionado: ${producto.nombre}")
        println("DEBUG: Estado anterior del producto seleccionado: ${_productoSeleccionado.value?.nombre}")
        _productoSeleccionado.value = producto
        println("DEBUG: Estado actual del producto seleccionado: ${_productoSeleccionado.value?.nombre}")
    }

    // Función para limpiar el producto seleccionado
    fun limpiarProductoSeleccionado() {
        println("DEBUG: Limpiando producto seleccionado")
        _productoSeleccionado.value = null
    }

    // Función para obtener el producto seleccionado (para debugging)
    fun obtenerProductoSeleccionado(): Producto? {
        val producto = _productoSeleccionado.value
        println("DEBUG: Obteniendo producto seleccionado: ${producto?.nombre}")
        return producto
    }

    // Función para eliminar un producto
    fun eliminarProducto(producto: Producto, onResult: (Boolean) -> Unit) {
        println("DEBUG: Intentando eliminar producto: ${producto.nombre}")
        
        // Buscar el documento por código de barras
        db.collection("productos")
            .whereEqualTo("codigo_barras", producto.codigo_barras)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documentId = documents.documents[0].id
                    db.collection("productos")
                        .document(documentId)
                        .delete()
                        .addOnSuccessListener {
                            println("DEBUG: Producto eliminado exitosamente")
                            
                            // Actualizar estado local
                            val listaActual = _productos.value.toMutableList()
                            listaActual.removeAll { it.codigo_barras == producto.codigo_barras }
                            _productos.value = listaActual
                            
                            onResult(true)
                        }
                        .addOnFailureListener { exception ->
                            println("ERROR: Error al eliminar producto: ${exception.message}")
                            onResult(false)
                        }
                } else {
                    println("ERROR: Producto no encontrado para eliminar")
                    onResult(false)
                }
            }
            .addOnFailureListener { exception ->
                println("ERROR: Error al buscar producto para eliminar: ${exception.message}")
                onResult(false)
            }
    }

    // Función para editar un producto
    fun editarProducto(productoOriginal: Producto, productoEditado: Producto, onResult: (Boolean) -> Unit) {
        println("DEBUG: Intentando editar producto: ${productoOriginal.nombre}")
        
        // Buscar el documento por código de barras
        db.collection("productos")
            .whereEqualTo("codigo_barras", productoOriginal.codigo_barras)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documentId = documents.documents[0].id
                    val productoParaGuardar = productoEditado.copy(
                        producto_id = documentId,
                        negocio_id = productoOriginal.negocio_id
                    )
                    
                    db.collection("productos")
                        .document(documentId)
                        .set(productoParaGuardar)
                        .addOnSuccessListener {
                            println("DEBUG: Producto editado exitosamente")
                            
                            // Actualizar estado local
                            val listaActual = _productos.value.toMutableList()
                            val index = listaActual.indexOfFirst { it.codigo_barras == productoOriginal.codigo_barras }
                            if (index != -1) {
                                listaActual[index] = productoParaGuardar
                                _productos.value = listaActual
                            }
                            
                            // Actualizar ProductoSeleccionadoManager si el producto editado es el seleccionado
                            val productoSeleccionado = ProductoSeleccionadoManager.obtenerProducto()
                            if (productoSeleccionado?.codigo_barras == productoOriginal.codigo_barras) {
                                ProductoSeleccionadoManager.seleccionarProducto(productoParaGuardar)
                                println("DEBUG: ProductoSeleccionadoManager actualizado con producto editado")
                            }
                            
                            onResult(true)
                        }
                        .addOnFailureListener { exception ->
                            println("ERROR: Error al editar producto: ${exception.message}")
                            onResult(false)
                        }
                } else {
                    println("ERROR: Producto no encontrado para editar")
                    onResult(false)
                }
            }
            .addOnFailureListener { exception ->
                println("ERROR: Error al buscar producto para editar: ${exception.message}")
                onResult(false)
            }
    }
} 