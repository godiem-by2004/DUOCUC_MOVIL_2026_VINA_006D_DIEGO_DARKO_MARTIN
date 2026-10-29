package com.example.agrogestion.util

import java.util.Locale
import kotlin.math.abs

/**
 * Muestra una coordenada con el hemisferio en letras y sin signo, ej:
 * (-32.8824, -71.2489) -> "32.8824° S, 71.2489° W".
 * (Antes se mostraba "-32.8824° S": el signo y la letra indicaban dos veces el hemisferio.)
 */
object FormatoCoordenada {

    fun texto(lat: Double, lon: Double): String {
        val ns = if (lat < 0) "S" else "N"
        val ew = if (lon < 0) "W" else "E"
        return String.format(Locale.US, "%.4f° %s, %.4f° %s", abs(lat), ns, abs(lon), ew)
    }
}
