package com.example.agrogestion.viewmodel

data class ValidacionCampo(
    val esValido: Boolean = true,
    val mensajeError: String? = null
)

object FormularioNutricionalValidator {

    const val PH_MIN = 3.5
    const val PH_MAX = 9.5
    const val CE_MIN = 0.05
    const val CE_MAX = 6.0
    const val N_MAX = 400.0
    const val P_MAX = 300.0
    const val K_MAX = 500.0
    const val CA_MAX = 300.0
    const val MG_MAX = 200.0

    private val REGEX_DECIMAL = Regex("^-?\\d{1,5}([.,]\\d{1,3})?$")

    fun parsearDecimal(valor: String): Double? {
        val limpio = valor.trim()
        if (!REGEX_DECIMAL.matches(limpio)) return null
        return limpio.replace(',', '.').toDoubleOrNull()
    }

    fun validarTextoObligatorio(valor: String, nombreCampo: String, minLargo: Int = 3): ValidacionCampo {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() -> ValidacionCampo(false, "El campo $nombreCampo es obligatorio")
            limpio.length < minLargo -> ValidacionCampo(false, "$nombreCampo debe tener al menos $minLargo caracteres")
            else -> ValidacionCampo(true, null)
        }
    }

    fun validarPh(valor: String): ValidacionCampo {
        if (valor.isBlank()) return ValidacionCampo(false, "Ingrese el pH medido en sonda/suelo")
        val numero = parsearDecimal(valor)
            ?: return ValidacionCampo(false, "Formato numérico inválido (ej: 6.8)")
        return when {
            numero < PH_MIN -> ValidacionCampo(false, "pH extremadamente ácido (< $PH_MIN). Verifique electrodo")
            numero > PH_MAX -> ValidacionCampo(false, "pH extremadamente alcalino (> $PH_MAX). Verifique muestra")
            else -> ValidacionCampo(true, null)
        }
    }

    fun validarConductividad(valor: String): ValidacionCampo {
        if (valor.isBlank()) return ValidacionCampo(false, "Ingrese la C.E. de agua/extracto (dS/m)")
        val numero = parsearDecimal(valor)
            ?: return ValidacionCampo(false, "Ingrese un decimal válido (ej: 1.4)")
        return when {
            numero < CE_MIN -> ValidacionCampo(false, "La C.E. debe ser mayor a $CE_MIN dS/m")
            numero > CE_MAX -> ValidacionCampo(false, "Peligro salino (> $CE_MAX dS/m). Revise dilución")
            else -> ValidacionCampo(true, null)
        }
    }

    fun validarNutriente(
        valor: String,
        simbolo: String,
        maxPermitido: Double
    ): ValidacionCampo {
        if (valor.isBlank()) return ValidacionCampo(false, "Indique aporte actual de $simbolo (kg/ha)")
        val numero = parsearDecimal(valor)
            ?: return ValidacionCampo(false, "Valor numérico inválido para $simbolo")
        return when {
            numero < 0.0 -> ValidacionCampo(false, "$simbolo no puede ser negativo")
            numero > maxPermitido -> ValidacionCampo(false, "$simbolo excede límite agronómico (${maxPermitido.toInt()} kg/ha)")
            else -> ValidacionCampo(true, null)
        }
    }
}
