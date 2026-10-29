package com.example.agrogestion

import com.example.agrogestion.data.local.RegistroEntity
import com.example.agrogestion.model.CatalogoAgronomico
import com.example.agrogestion.viewmodel.AgroDashboardUiState
import com.example.agrogestion.viewmodel.Campo
import com.example.agrogestion.viewmodel.FormularioUiState
import com.example.agrogestion.viewmodel.LoginUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas de la lógica de estado (IE 2.2.2): formulario y dashboard. */
class EstadoUiTest {

    private fun formularioCompleto() = FormularioUiState(
        cuartel = "Cuartel 4", ph = "6.8", ec = "1.4",
        nitrogeno = "118", fosforo = "45", potasio = "165", calcio = "60", magnesio = "25",
        observacionesFoliares = "Clorosis leve"
    )

    private fun registro(predio: String, ph: Double = 6.5) = RegistroEntity(
        predio = predio, ubicacion = "Quillota", cuartel = "C1", cultivo = "Palto Hass",
        etapa = "Engorde de Fruto", ph = ph, ec = 1.4, nitrogeno = 120.0, fosforo = 45.0,
        potasio = 180.0, calcio = 60.0, magnesio = 25.0, latitud = 0.0, longitud = 0.0,
        observacionesFoliares = "ok", fechaMillis = 0L
    )

    @Test fun formulario_inicial_no_es_valido_pero_tampoco_muestra_errores() {
        val s = FormularioUiState()
        assertFalse(s.formularioCompletoValido)
        assertTrue(s.valPh.esValido)          // aún no tocado -> sin error visible
        assertTrue(s.valCuartel.esValido)
    }

    @Test fun error_aparece_solo_en_el_campo_tocado() {
        val s = FormularioUiState(ph = "abc", camposTocados = setOf(Campo.PH))
        assertFalse(s.valPh.esValido)
        assertTrue(s.valEc.esValido)          // EC vacío pero no tocado
    }

    @Test fun intentar_guardar_muestra_todos_los_errores() {
        val s = FormularioUiState(mostrarErroresForzados = true)
        assertFalse(s.valPh.esValido)
        assertFalse(s.valEc.esValido)
        assertFalse(s.valCuartel.esValido)
        assertFalse(s.valObservaciones.esValido)
    }

    @Test fun formulario_completo_es_valido() {
        assertTrue(formularioCompleto().formularioCompletoValido)
    }

    @Test fun un_solo_campo_invalido_bloquea_el_guardado() {
        assertFalse(formularioCompleto().copy(ph = "NaN").formularioCompletoValido)
        assertFalse(formularioCompleto().copy(magnesio = "").formularioCompletoValido)
    }

    @Test fun ultimo_registro_no_mezcla_datos_de_otro_predio() {
        val ui = AgroDashboardUiState(
            predioSeleccionado = CatalogoAgronomico.prediosDisponibles.first(), // San Isidro
            registros = listOf(registro("Fundo Las Palmas"))
        )
        assertNull(ui.ultimoRegistroPredio)
        assertNull(ui.phActual)
        assertTrue(ui.balancesNutricionales.isEmpty())
    }

    @Test fun balance_calcula_diferencia_porcentual_contra_el_requerimiento() {
        val san = CatalogoAgronomico.prediosDisponibles.first()
        val ui = AgroDashboardUiState(predioSeleccionado = san, registros = listOf(registro(san.nombre)))
        val n = ui.balancesNutricionales.first { it.simbolo == "N" }
        assertEquals(0.0, n.diferenciaPorcentaje, 0.0001)      // 120 real vs 120 requerido
        val k = ui.balancesNutricionales.first { it.simbolo == "K" }
        assertEquals(0.0, k.diferenciaPorcentaje, 0.0001)      // 180 vs 180
        assertFalse(n.requiereAjuste)
    }

    @Test fun pendientes_por_sincronizar_cuenta_solo_los_no_sincronizados() {
        val ui = AgroDashboardUiState(
            registros = listOf(registro("A"), registro("B").copy(sincronizado = true))
        )
        assertEquals(1, ui.pendientesSincronizar)
    }

    @Test fun formulario_no_esta_guardando_por_defecto() {
        assertFalse(FormularioUiState().guardando)
    }

    @Test fun filtro_vacio_devuelve_todos_los_registros() {
        val ui = AgroDashboardUiState(registros = listOf(registro("A"), registro("B")), filtroHistorial = "  ")
        assertEquals(2, ui.registrosFiltrados.size)
    }

    @Test fun filtro_por_cultivo_ignora_mayusculas() {
        val tomate = registro("Agrícola El Boldo").copy(cultivo = "Tomate Indeterminado")
        val ui = AgroDashboardUiState(registros = listOf(registro("A"), tomate), filtroHistorial = "TOMATE")
        assertEquals(listOf(tomate), ui.registrosFiltrados)
    }

    @Test fun requerimiento_de_fertirriego_usa_el_cultivo_de_la_ultima_muestra() {
        val san = CatalogoAgronomico.prediosDisponibles.first() // catálogo: Palto Hass
        val tomate = registro(san.nombre).copy(cultivo = "Tomate Indeterminado", etapa = "Maduración")
        val ui = AgroDashboardUiState(predioSeleccionado = san, registros = listOf(tomate))
        assertEquals("Tomate Indeterminado", ui.requerimientoActual.cultivo)
    }

    @Test fun requerimiento_sin_muestras_usa_el_catalogo_del_predio() {
        val san = CatalogoAgronomico.prediosDisponibles.first()
        val ui = AgroDashboardUiState(predioSeleccionado = san)
        assertEquals(san.cultivo, ui.requerimientoActual.cultivo)
        assertEquals(san.superficieHa * san.laminaRiegoM3Ha, ui.volumenRiegoTotalM3, 0.0001)
    }

    @Test fun login_muestra_errores_solo_al_forzar_o_tocar() {
        val inicial = LoginUiState()
        assertTrue(inicial.valCorreo.esValido)
        assertFalse(inicial.formularioValido)
        val forzado = inicial.copy(mostrarErroresForzados = true)
        assertFalse(forzado.valCorreo.esValido)
        assertFalse(forzado.valClave.esValido)
        assertTrue(LoginUiState(correo = "asesor@agrogestion.cl", clave = "Agro2026").formularioValido)
    }
}
