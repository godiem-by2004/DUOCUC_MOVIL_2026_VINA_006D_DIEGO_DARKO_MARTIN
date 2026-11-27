package com.example.agrogestion.data.remote

import com.example.agrogestion.data.local.RegistroEntity
import com.google.gson.annotations.SerializedName

data class RegistroDto(
    val id: Long? = null,
    val localId: Long? = null,
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
    val precisionMetros: Float? = null,
    val gpsDesdeSensor: Boolean = false,
    val observacionesFoliares: String,
    val recomendacion: String = "",
    val fechaMillis: Long
)

fun RegistroEntity.toDto() = RegistroDto(
    localId = id,
    predio = predio,
    ubicacion = ubicacion,
    cuartel = cuartel,
    cultivo = cultivo,
    etapa = etapa,
    ph = ph,
    ec = ec,
    nitrogeno = nitrogeno,
    fosforo = fosforo,
    potasio = potasio,
    calcio = calcio,
    magnesio = magnesio,
    latitud = latitud,
    longitud = longitud,
    precisionMetros = precisionMetros,
    gpsDesdeSensor = gpsDesdeSensor,
    observacionesFoliares = observacionesFoliares,
    recomendacion = recomendacion,
    fechaMillis = fechaMillis
)

fun RegistroDto.toEntity() = RegistroEntity(
    id = localId ?: id ?: 0L,
    predio = predio,
    ubicacion = ubicacion,
    cuartel = cuartel,
    cultivo = cultivo,
    etapa = etapa,
    ph = ph,
    ec = ec,
    nitrogeno = nitrogeno,
    fosforo = fosforo,
    potasio = potasio,
    calcio = calcio,
    magnesio = magnesio,
    latitud = latitud,
    longitud = longitud,
    precisionMetros = precisionMetros,
    gpsDesdeSensor = gpsDesdeSensor,
    observacionesFoliares = observacionesFoliares,
    recomendacion = recomendacion,
    fechaMillis = fechaMillis,
    sincronizado = true
)

// DTOs para API Externa Open-Meteo (Clima agroclimático en tiempo real)
data class OpenMeteoResponse(
    val current: ClimaActualDto?
)

data class ClimaActualDto(
    @SerializedName("temperature_2m") val temperaturaCelsius: Double,
    @SerializedName("relative_humidity_2m") val humedadRelativa: Int,
    @SerializedName("wind_speed_10m") val vientoKmh: Double,
    val precipitation: Double
)