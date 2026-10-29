package com.example.agrogestion

import com.example.agrogestion.util.FormatoCoordenada
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatoCoordenadaTest {

    @Test fun hemisferio_sur_oeste_sin_signo_negativo() {
        assertEquals("32.8824° S, 71.2489° W", FormatoCoordenada.texto(-32.8824, -71.2489))
    }

    @Test fun hemisferio_norte_este() {
        assertEquals("40.4168° N, 3.7038° E", FormatoCoordenada.texto(40.4168, 3.7038))
    }

    @Test fun redondea_a_cuatro_decimales() {
        assertEquals("32.8271° S, 71.2294° W", FormatoCoordenada.texto(-32.827149, -71.229412))
    }
}
