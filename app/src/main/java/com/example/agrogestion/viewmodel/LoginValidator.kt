package com.example.agrogestion.viewmodel

/**
 * Reglas de validación del formulario de login (IE 2.1.1 / IE 2.2.1).
 * Kotlin puro (sin Android): se prueba con tests JVM, igual que FormularioNutricionalValidator.
 * No se usa android.util.Patterns porque no existe en los tests unitarios.
 */
object LoginValidator {

    const val CLAVE_MIN = 6

    private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validarCorreo(valor: String): ValidacionCampo {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() -> ValidacionCampo(false, "Ingrese su correo")
            !REGEX_CORREO.matches(limpio) -> ValidacionCampo(false, "Formato de correo inválido (ej: nombre@dominio.cl)")
            else -> ValidacionCampo(true, null)
        }
    }

    fun validarClave(valor: String): ValidacionCampo = when {
        valor.isEmpty() -> ValidacionCampo(false, "Ingrese su contraseña")
        valor.length < CLAVE_MIN -> ValidacionCampo(false, "La contraseña debe tener al menos $CLAVE_MIN caracteres")
        else -> ValidacionCampo(true, null)
    }
}
