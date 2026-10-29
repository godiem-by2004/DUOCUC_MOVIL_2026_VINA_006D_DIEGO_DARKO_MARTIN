package com.example.agrogestion.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Fila de la tabla `registros_terreno` (una muestra tomada en terreno).
 * Sin valores "de relleno": cada dato guardado viene del formulario, del GPS o de la cámara.
 */
@Entity(tableName = "registros_terreno")
data class RegistroEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val predio: String,
    val ubicacion: String,
    val cuartel: String,
    val cultivo: String,
    val etapa: String,
    val ph: Double,
    val ec: Double,
    val nitrogeno: Double,
    val fosforo: Double,
    val potasio: Double,
    val calcio: Double,
    val magnesio: Double,
    val latitud: Double,
    val longitud: Double,
    /** Precisión horizontal en metros; null si la coordenada no vino del sensor. */
    val precisionMetros: Float? = null,
    /** true = coordenada leída del GPS/red del dispositivo; false = referencia del predio. */
    val gpsDesdeSensor: Boolean = false,
    /** Ruta absoluta del JPG guardado en el almacenamiento interno de la app (null = sin foto). */
    val fotoUri: String? = null,
    val observacionesFoliares: String,
    val recomendacion: String = "",
    /** Epoch en milisegundos (zona horaria del dispositivo al mostrarla). */
    val fechaMillis: Long,
    val sincronizado: Boolean = false
)
