package com.example.agrogestion

import com.example.agrogestion.viewmodel.LoginValidator as L
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas de las reglas del login (IE 2.1.1). JVM puras: no necesitan emulador. */
class LoginValidatorTest {

    @Test fun correo_vacio_es_invalido() {
        val r = L.validarCorreo("   ")
        assertFalse(r.esValido)
        assertEquals("Ingrese su correo", r.mensajeError)
    }

    @Test fun correo_sin_arroba_es_invalido() {
        val r = L.validarCorreo("asesor.agrogestion.cl")
        assertFalse(r.esValido)
        assertEquals("Formato de correo inválido (ej: nombre@dominio.cl)", r.mensajeError)
    }

    @Test fun correo_sin_dominio_es_invalido() {
        assertFalse(L.validarCorreo("asesor@").esValido)
        assertFalse(L.validarCorreo("asesor@agrogestion").esValido)
    }

    @Test fun correo_valido_con_espacios_alrededor() {
        assertTrue(L.validarCorreo("  asesor@agrogestion.cl ").esValido)
    }

    @Test fun clave_vacia_es_invalida() {
        val r = L.validarClave("")
        assertFalse(r.esValido)
        assertEquals("Ingrese su contraseña", r.mensajeError)
    }

    @Test fun clave_de_5_caracteres_es_invalida() {
        val r = L.validarClave("Agro2")
        assertFalse(r.esValido)
        assertEquals("La contraseña debe tener al menos 6 caracteres", r.mensajeError)
    }

    @Test fun clave_de_6_caracteres_es_valida() {
        assertTrue(L.validarClave("Agro20").esValido)
    }
}
