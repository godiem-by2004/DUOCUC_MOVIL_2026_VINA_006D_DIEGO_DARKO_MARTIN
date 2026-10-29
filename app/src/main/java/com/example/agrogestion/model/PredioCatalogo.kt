package com.example.agrogestion.model

data class PredioInfo(
    val id: String,
    val nombre: String,
    val ubicacion: String,
    val cuartelPrincipal: String,
    val cultivo: String,
    val superficieHa: Double,
    val sistemaRiego: String,
    val laminaRiegoM3Ha: Double,
    val etapaActual: String,
    val subEtapaDetalle: String,
    val vigorEstado: String,
    val proximoFertirriegoHora: String,
    val proximoFertirriegoSector: String,
    val latitudBase: Double,
    val longitudBase: Double
)

data class RequerimientoNutricional(
    val cultivo: String,
    val etapa: String,
    val nRequerido: Double,
    val pRequerido: Double,
    val kRequerido: Double,
    val caRequerido: Double,
    val mgRequerido: Double,
    val phOptimoRiego: Double,
    val ceProyectada: Double
)

data class BalanceElemento(
    val simbolo: String,
    val nombre: String,
    val aporteRealKgHa: Double,
    val requeridoKgHa: Double,
    val diferenciaPorcentaje: Double,
    val estadoTexto: String,
    val requiereAjuste: Boolean
)

object CatalogoAgronomico {
    val prediosDisponibles = listOf(
        PredioInfo(
            id = "san_isidro",
            nombre = "Fundo San Isidro",
            ubicacion = "Quillota, Región de Valparaíso",
            cuartelPrincipal = "Cuartel 4",
            cultivo = "Palto Hass",
            superficieHa = 12.5,
            sistemaRiego = "Goteo Doble Línea",
            laminaRiegoM3Ha = 45.0,
            etapaActual = "Engorde de Fruto",
            subEtapaDetalle = "Fase II · Calibre proyectado 50-55mm",
            vigorEstado = "Vigor Alto",
            proximoFertirriegoHora = "Hoy 16:30 hrs",
            proximoFertirriegoSector = "Sector Norte (N-P-K balanceado)",
            latitudBase = -32.8824,
            longitudBase = -71.2489
        ),
        PredioInfo(
            id = "las_palmas",
            nombre = "Fundo Las Palmas",
            ubicacion = "La Cruz, Región de Valparaíso",
            cuartelPrincipal = "Cuartel 2",
            cultivo = "Cítricos (Mandarino W. Murcott)",
            superficieHa = 8.4,
            sistemaRiego = "Microaspersión Compensada",
            laminaRiegoM3Ha = 38.0,
            etapaActual = "Floración",
            subEtapaDetalle = "Fase I · Cuaja inicial 85% activa",
            vigorEstado = "Vigor Óptimo",
            proximoFertirriegoHora = "Hoy 18:00 hrs",
            proximoFertirriegoSector = "Sector Oriente (Refuerzo Fósforo-Calcio)",
            latitudBase = -32.8271,
            longitudBase = -71.2294
        ),
        PredioInfo(
            id = "el_boldo",
            nombre = "Agrícola El Boldo",
            ubicacion = "Hijuelas, Región de Valparaíso",
            cuartelPrincipal = "Invernadero 7",
            cultivo = "Tomate Indeterminado",
            superficieHa = 4.2,
            sistemaRiego = "Cinta Goteo Alta Frecuencia",
            laminaRiegoM3Ha = 52.0,
            etapaActual = "Maduración",
            subEtapaDetalle = "Racimo 5 · Coloración uniforme",
            vigorEstado = "Vigor Alto",
            proximoFertirriegoHora = "Mañana 07:30 hrs",
            proximoFertirriegoSector = "Naves 1 a 4 (Alto Potasio)",
            latitudBase = -32.7985,
            longitudBase = -71.1432
        )
    )

    val cultivosDisponibles = listOf(
        "Palto Hass",
        "Cítricos (Mandarino W. Murcott)",
        "Tomate Indeterminado"
    )

    val etapasFenologicas = listOf(
        "Floración",
        "Engorde de Fruto",
        "Maduración"
    )

    fun obtenerRequerimiento(cultivo: String, etapa: String): RequerimientoNutricional {
        return when {
            cultivo.contains("Palto", ignoreCase = true) && etapa.contains("Engorde", ignoreCase = true) ->
                RequerimientoNutricional(cultivo, etapa, 120.0, 45.0, 180.0, 60.0, 25.0, 5.9, 1.85)
            cultivo.contains("Palto", ignoreCase = true) && etapa.contains("Floración", ignoreCase = true) ->
                RequerimientoNutricional(cultivo, etapa, 95.0, 60.0, 130.0, 70.0, 30.0, 6.0, 1.65)
            cultivo.contains("Cítricos", ignoreCase = true) ->
                RequerimientoNutricional(cultivo, etapa, 110.0, 50.0, 160.0, 65.0, 28.0, 6.1, 1.75)
            cultivo.contains("Tomate", ignoreCase = true) ->
                RequerimientoNutricional(cultivo, etapa, 140.0, 55.0, 220.0, 80.0, 35.0, 5.8, 2.20)
            else ->
                RequerimientoNutricional(cultivo, etapa, 120.0, 45.0, 180.0, 60.0, 25.0, 5.9, 1.85)
        }
    }
}
