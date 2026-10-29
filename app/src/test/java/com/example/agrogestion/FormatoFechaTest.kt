package com.example.agrogestion

import com.example.agrogestion.util.FormatoFecha
import java.util.Calendar
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatoFechaTest {
    private val utc = TimeZone.getTimeZone("UTC")

    private fun t(anio: Int, mes: Int, dia: Int, h: Int, m: Int): Long =
        Calendar.getInstance(utc).apply { clear(); set(anio, mes - 1, dia, h, m) }.timeInMillis

    private val ahora = t(2026, 10, 28, 15, 0)

    @Test fun mismo_dia_dice_Hoy() =
        assertEquals("Hoy 11:24", FormatoFecha.relativa(t(2026, 10, 28, 11, 24), ahora, utc))

    @Test fun dia_anterior_dice_Ayer_aunque_pasen_menos_de_24_horas() =
        assertEquals("Ayer 23:50", FormatoFecha.relativa(t(2026, 10, 27, 23, 50), ahora, utc))

    @Test fun mas_antiguo_muestra_fecha_completa() =
        assertEquals("25/10/2026 09:15", FormatoFecha.relativa(t(2026, 10, 25, 9, 15), ahora, utc))
}
