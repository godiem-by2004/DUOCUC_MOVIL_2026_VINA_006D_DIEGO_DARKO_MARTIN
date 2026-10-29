package com.example.agrogestion.repository

import com.example.agrogestion.data.local.RegistroDao
import com.example.agrogestion.data.local.RegistroEntity
import com.example.agrogestion.model.CatalogoAgronomico
import kotlinx.coroutines.flow.Flow

class AgronomicoRepository(private val dao: RegistroDao) {

    val registros: Flow<List<RegistroEntity>> = dao.obtenerTodos()

    /**
     * Datos de DEMOSTRACIÓN (dataset ficticio del caso). Se insertan solo si la tabla está vacía.
     * Las fechas son relativas a "ahora" para que Hoy/Ayer siempre sean coherentes.
     */
    suspend fun inicializarSemillaSiVacio() {
        if (dao.contarRegistros() != 0) return
        val ahora = System.currentTimeMillis()
        val hora = 3_600_000L

        fun demo(
            predioId: String, cuartel: String, cultivo: String, etapa: String,
            ph: Double, ec: Double, n: Double, p: Double, k: Double, ca: Double, mg: Double,
            latDelta: Double, lonDelta: Double, obs: String, haceHoras: Long
        ): RegistroEntity {
            val predio = CatalogoAgronomico.prediosDisponibles.first { it.id == predioId }
            return RegistroEntity(
                predio = predio.nombre,
                ubicacion = predio.ubicacion,
                cuartel = cuartel,
                cultivo = cultivo,
                etapa = etapa,
                ph = ph, ec = ec,
                nitrogeno = n, fosforo = p, potasio = k, calcio = ca, magnesio = mg,
                latitud = predio.latitudBase + latDelta,
                longitud = predio.longitudBase + lonDelta,
                observacionesFoliares = obs,
                recomendacion = generarRecomendacion(cultivo, etapa, n, p, k),
                fechaMillis = ahora - haceHoras * hora
            )
        }

        listOf(
            demo("san_isidro", "Cuartel 4", "Palto Hass", "Engorde de Fruto",
                6.8, 1.4, 118.0, 45.0, 165.0, 60.0, 25.0, 0.0, 0.0,
                "Ligera clorosis intervenal en hojas basales; ajuste de K2O sugerido.", 1),
            demo("san_isidro", "Cuartel 2 Norte", "Palto Hass", "Engorde de Fruto",
                6.6, 1.5, 112.0, 44.0, 170.0, 58.0, 24.0, 0.0014, 0.0014,
                "Lámina foliar sana, turgencia óptima en brotes.", 3),
            demo("las_palmas", "Cuartel 2", "Cítricos (Mandarino W. Murcott)", "Floración",
                6.4, 1.3, 104.0, 48.0, 152.0, 64.0, 27.0, 0.0, 0.0,
                "Floración abundante, requerimiento cálcico en rango.", 20),
            demo("el_boldo", "Invernadero 7", "Tomate Indeterminado", "Maduración",
                6.1, 2.1, 135.0, 54.0, 205.0, 78.0, 33.0, 0.0, 0.0,
                "Borde foliar activo, reforzar potasio para llenado.", 27)
        ).forEach { dao.insertar(it) }
    }

    /**
     * Guarda una muestra nueva. El repositorio es la única capa que agrega lo que depende
     * de reglas de negocio (recomendación) y de la hora real del guardado.
     */
    suspend fun guardarMuestra(muestra: RegistroEntity): Long {
        val conCalculos = muestra.copy(
            observacionesFoliares = muestra.observacionesFoliares.ifBlank { "Muestra foliar verificada en terreno" },
            recomendacion = generarRecomendacion(
                muestra.cultivo, muestra.etapa,
                muestra.nitrogeno, muestra.fosforo, muestra.potasio
            ),
            fechaMillis = System.currentTimeMillis(),
            sincronizado = false
        )
        return dao.insertar(conCalculos)
    }

    suspend fun actualizarObservaciones(id: Long, texto: String) {
        dao.actualizarObservaciones(id, texto.trim())
    }

    suspend fun eliminarRegistro(id: Long) {
        dao.eliminarPorId(id)
    }

    /** SIMULADO: marca como sincronizado. En la EFT se reemplaza por Retrofit + Spring Boot. */
    suspend fun sincronizarPendientes() {
        dao.marcarTodosSincronizados()
    }

    private fun generarRecomendacion(
        cultivo: String,
        etapa: String,
        n: Double,
        p: Double,
        k: Double
    ): String {
        val req = CatalogoAgronomico.obtenerRequerimiento(cultivo, etapa)
        val deficN = (req.nRequerido - n).coerceAtLeast(0.0)
        val deficP = (req.pRequerido - p).coerceAtLeast(0.0)
        val deficK = (req.kRequerido - k).coerceAtLeast(0.0)

        return if (deficN == 0.0 && deficP == 0.0 && deficK == 0.0) {
            "Balance óptimo para $cultivo ($etapa). Mantener lámina estándar con pH ${req.phOptimoRiego}."
        } else {
            "Ajuste fertirriego ($etapa): Inyectar +${deficN.toInt()} kg/ha N, +${deficP.toInt()} kg/ha P₂O₅, +${deficK.toInt()} kg/ha K₂O."
        }
    }
}
