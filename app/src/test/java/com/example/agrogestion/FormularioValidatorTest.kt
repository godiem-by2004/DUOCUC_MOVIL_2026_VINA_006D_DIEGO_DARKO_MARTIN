package com.example.agrogestion

import com.example.agrogestion.viewmodel.FormularioNutricionalValidator as V
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas de las reglas de validación (IE 2.1.1 / 2.2.1). Son JVM puras: no necesitan emulador. */
class FormularioValidatorTest {

    @Test fun ph_valido_con_punto_y_con_coma() {
        assertTrue(V.validarPh("6.8").esValido)
        assertTrue(V.validarPh("6,8").esValido)
    }

    @Test fun ph_vacio_es_invalido_con_mensaje() {
        val r = V.validarPh("  ")
        assertFalse(r.esValido)
        assertNotNull(r.mensajeError)
    }

    @Test fun ph_rechaza_NaN_infinito_y_notacion_cientifica() {
        // Antes "NaN" pasaba como válido y se guardaba NaN en la base de datos.
        listOf("NaN", "Infinity", "-Infinity", "1e1", "0x1p3", "6.8abc", "6..8", "abc").forEach {
            assertFalse("'$it' debería ser inválido", V.validarPh(it).esValido)
        }
    }

    @Test fun ph_limites_exactos() {
        assertTrue(V.validarPh("3.5").esValido)
        assertFalse(V.validarPh("3.4").esValido)
        assertTrue(V.validarPh("9.5").esValido)
        assertFalse(V.validarPh("9.6").esValido)
    }

    @Test fun conductividad_limites() {
        assertFalse(V.validarConductividad("0.04").esValido)
        assertTrue(V.validarConductividad("0.05").esValido)
        assertTrue(V.validarConductividad("6.0").esValido)
        assertFalse(V.validarConductividad("6.1").esValido)
        assertFalse(V.validarConductividad("NaN").esValido)
    }

    @Test fun nutriente_negativo_vacio_y_sobre_el_maximo() {
        assertFalse(V.validarNutriente("", "N", 400.0).esValido)
        assertEquals("N no puede ser negativo", V.validarNutriente("-5", "N", 400.0).mensajeError)
        assertTrue(V.validarNutriente("400", "N", 400.0).esValido)
        assertFalse(V.validarNutriente("401", "N", 400.0).esValido)
        assertFalse(V.validarNutriente("NaN", "N", 400.0).esValido)
    }

    @Test fun texto_obligatorio_respeta_largo_minimo_y_espacios() {
        assertFalse(V.validarTextoObligatorio("   ", "Cuartel").esValido)
        assertFalse(V.validarTextoObligatorio("ab", "Cuartel", 3).esValido)
        assertTrue(V.validarTextoObligatorio(" Cuartel 4 ", "Cuartel", 3).esValido)
    }

    @Test fun parsearDecimal_devuelve_null_si_el_formato_es_invalido() {
        assertEquals(6.8, V.parsearDecimal("6,8")!!, 0.0001)
        assertNull(V.parsearDecimal("NaN"))
        assertNull(V.parsearDecimal(""))
    }
}
