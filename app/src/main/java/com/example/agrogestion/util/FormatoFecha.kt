package com.example.agrogestion.util

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Convierte la marca de tiempo (epoch en milisegundos) guardada en Room a un texto legible.
 * Se guarda un Long y no un String para poder ordenar/filtrar por fecha y para que
 * "Hoy" / "Ayer" siempre sean correctos (antes quedaba escrito "Hoy" para siempre).
 *
 * Es Kotlin puro (sin Context) para poder probarlo con tests unitarios.
 */
object FormatoFecha {

    fun relativa(
        millis: Long,
        ahora: Long = System.currentTimeMillis(),
        zona: TimeZone = TimeZone.getDefault()
    ): String {
        val hora = String.format(Locale.US, "%02d:%02d", campo(millis, zona, Calendar.HOUR_OF_DAY), campo(millis, zona, Calendar.MINUTE))
        return when (diasEntre(millis, ahora, zona)) {
            0 -> "Hoy $hora"
            1 -> "Ayer $hora"
            else -> {
                val dia = campo(millis, zona, Calendar.DAY_OF_MONTH)
                val mes = campo(millis, zona, Calendar.MONTH) + 1
                val anio = campo(millis, zona, Calendar.YEAR)
                String.format(Locale.US, "%02d/%02d/%d %s", dia, mes, anio, hora)
            }
        }
    }

    /** Días de calendario completos entre dos instantes (0 = mismo día, 1 = día anterior...). */
    internal fun diasEntre(millis: Long, ahora: Long, zona: TimeZone): Int {
        fun inicioDia(t: Long): Long = Calendar.getInstance(zona).apply {
            timeInMillis = t
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val dif = inicioDia(ahora) - inicioDia(millis)
        return Math.round(dif / 86_400_000.0).toInt()
    }

    private fun campo(millis: Long, zona: TimeZone, campo: Int): Int =
        Calendar.getInstance(zona).apply { timeInMillis = millis }.get(campo)
}
