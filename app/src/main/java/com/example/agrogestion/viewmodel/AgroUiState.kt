package com.example.agrogestion.viewmodel

import com.example.agrogestion.data.local.RegistroEntity
import com.example.agrogestion.model.BalanceElemento
import com.example.agrogestion.model.CatalogoAgronomico
import com.example.agrogestion.model.PredioInfo
import com.example.agrogestion.model.RequerimientoNutricional

object Campo {
    const val CUARTEL = "cuartel"
    const val PH = "ph"
    const val EC = "ec"
    const val N = "n"
    const val P = "p"
    const val K = "k"
    const val CA = "ca"
    const val MG = "mg"
    const val OBSERVACIONES = "observaciones"
}

enum class EstadoGps { REFERENCIA_PREDIO, BUSCANDO, SENSOR, ERROR }

data class FormularioUiState(
    val predio: String = CatalogoAgronomico.prediosDisponibles.first().nombre,
    val ubicacion: String = CatalogoAgronomico.prediosDisponibles.first().ubicacion,
    val cuartel: String = "",
    val cultivo: String = CatalogoAgronomico.prediosDisponibles.first().cultivo,
    val etapa: String = CatalogoAgronomico.prediosDisponibles.first().etapaActual,
    val ph: String = "",
    val ec: String = "",
    val nitrogeno: String = "",
    val fosforo: String = "",
    val potasio: String = "",
    val calcio: String = "",
    val magnesio: String = "",
    val observacionesFoliares: String = "",

    val latitud: Double = CatalogoAgronomico.prediosDisponibles.first().latitudBase,
    val longitud: Double = CatalogoAgronomico.prediosDisponibles.first().longitudBase,
    val precisionMetros: Float? = null,
    val gpsEstado: EstadoGps = EstadoGps.REFERENCIA_PREDIO,

    val fotoUri: String? = null,

    val mensajeGps: String? = null,
    val mensajeCamara: String? = null,

    val camposTocados: Set<String> = emptySet(),
    val mostrarErroresForzados: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val guardando: Boolean = false
) {
    private val v = FormularioNutricionalValidator

    private val rawCuartel get() = v.validarTextoObligatorio(cuartel, "Cuartel / Polígono", 3)
    private val rawPh get() = v.validarPh(ph)
    private val rawEc get() = v.validarConductividad(ec)
    private val rawN get() = v.validarNutriente(nitrogeno, "Nitrógeno (N)", v.N_MAX)
    private val rawP get() = v.validarNutriente(fosforo, "Fósforo (P₂O₅)", v.P_MAX)
    private val rawK get() = v.validarNutriente(potasio, "Potasio (K₂O)", v.K_MAX)
    private val rawCa get() = v.validarNutriente(calcio, "Calcio (CaO)", v.CA_MAX)
    private val rawMg get() = v.validarNutriente(magnesio, "Magnesio (MgO)", v.MG_MAX)
    private val rawObs get() = v.validarTextoObligatorio(observacionesFoliares, "Diagnóstico foliar", 5)

    val formularioCompletoValido: Boolean
        get() = listOf(rawCuartel, rawPh, rawEc, rawN, rawP, rawK, rawCa, rawMg, rawObs).all { it.esValido }

    private fun visible(campo: String, r: ValidacionCampo): ValidacionCampo =
        if (mostrarErroresForzados || campo in camposTocados) r else ValidacionCampo()

    val valCuartel get() = visible(Campo.CUARTEL, rawCuartel)
    val valPh get() = visible(Campo.PH, rawPh)
    val valEc get() = visible(Campo.EC, rawEc)
    val valNitrogeno get() = visible(Campo.N, rawN)
    val valFosforo get() = visible(Campo.P, rawP)
    val valPotasio get() = visible(Campo.K, rawK)
    val valCalcio get() = visible(Campo.CA, rawCa)
    val valMagnesio get() = visible(Campo.MG, rawMg)
    val valObservaciones get() = visible(Campo.OBSERVACIONES, rawObs)
}

data class AgroDashboardUiState(
    val predioSeleccionado: PredioInfo = CatalogoAgronomico.prediosDisponibles.first(),
    val registros: List<RegistroEntity> = emptyList(),
    val filtroHistorial: String = "",
    val nombreAsesor: String = "Ing. Agrónomo en Turno",
    val sincronizando: Boolean = false,
    val mensajeSincronizacion: String? = null
) {
    val pendientesSincronizar: Int
        get() = registros.count { !it.sincronizado }

    val ultimoRegistroPredio: RegistroEntity?
        get() = registros.firstOrNull { it.predio == predioSeleccionado.nombre }

    val phActual: Double? get() = ultimoRegistroPredio?.ph

    val ceActual: Double? get() = ultimoRegistroPredio?.ec

    val registrosFiltrados: List<RegistroEntity>
        get() {
            val q = filtroHistorial.trim()
            if (q.isEmpty()) return registros
            return registros.filter {
                it.predio.contains(q, ignoreCase = true) ||
                        it.cultivo.contains(q, ignoreCase = true) ||
                        it.cuartel.contains(q, ignoreCase = true)
            }
        }

    val requerimientoActual: RequerimientoNutricional
        get() {
            val reg = ultimoRegistroPredio
            return CatalogoAgronomico.obtenerRequerimiento(
                reg?.cultivo ?: predioSeleccionado.cultivo,
                reg?.etapa ?: predioSeleccionado.etapaActual
            )
        }

    val volumenRiegoTotalM3: Double
        get() = predioSeleccionado.superficieHa * predioSeleccionado.laminaRiegoM3Ha

    val balancesNutricionales: List<BalanceElemento>
        get() {
            val reg = ultimoRegistroPredio ?: return emptyList()
            val req = CatalogoAgronomico.obtenerRequerimiento(reg.cultivo, reg.etapa)
            return listOf(
                crearBalance("N", "Nitrógeno Total", reg.nitrogeno, req.nRequerido),
                crearBalance("P", "Fósforo (P₂O₅)", reg.fosforo, req.pRequerido),
                crearBalance("K", "Potasio (K₂O)", reg.potasio, req.kRequerido),
                crearBalance("Ca", "Calcio (CaO)", reg.calcio, req.caRequerido),
                crearBalance("Mg", "Magnesio (MgO)", reg.magnesio, req.mgRequerido)
            )
        }

    private fun crearBalance(
        simbolo: String,
        nombre: String,
        real: Double,
        requerido: Double
    ): BalanceElemento {
        val diffPct = if (requerido > 0) ((real - requerido) / requerido) * 100.0 else 0.0
        val diffRedondeado = kotlin.math.round(diffPct * 10.0) / 10.0
        val requiereAjuste = kotlin.math.abs(diffRedondeado) > 5.0
        val estadoTexto = when {
            kotlin.math.abs(diffRedondeado) < 0.2 -> "0% (Exacto)"
            requiereAjuste -> "${if (diffRedondeado > 0) "+" else ""}$diffRedondeado% (Ajustar)"
            else -> "${if (diffRedondeado > 0) "+" else ""}$diffRedondeado% (Óptimo)"
        }
        return BalanceElemento(
            simbolo = simbolo,
            nombre = nombre,
            aporteRealKgHa = real,
            requeridoKgHa = requerido,
            diferenciaPorcentaje = diffRedondeado,
            estadoTexto = estadoTexto,
            requiereAjuste = requiereAjuste
        )
    }
}
