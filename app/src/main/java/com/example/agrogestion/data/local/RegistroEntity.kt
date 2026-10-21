package com.example.agrogestion.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey

// clase de registro de terreno __ la pueden modificar si gustan

@Entity(tableName = "registros_terreno")
data class RegistroEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val predio: String,
    val cultivo: String,
    val etapa: String,
    val ph: Double,
    val ec: Double,
    val nitrogeno: Double,
    val fosforo: Double,
    val potasio: Double,
    val fotoUri: String? = null,
    val recomendacion: String = "",
    val sincronizado: Boolean = false
)
