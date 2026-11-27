package com.example.agrogestion.repository

import com.example.agrogestion.data.local.RegistroDao
import com.example.agrogestion.data.local.RegistroEntity
import com.example.agrogestion.data.remote.AgroApiService
import com.example.agrogestion.data.remote.CatalogoApiService
import com.example.agrogestion.data.remote.ClimaActualDto
import com.example.agrogestion.data.remote.ClimaApiService
import com.example.agrogestion.data.remote.RetrofitClient
import com.example.agrogestion.data.remote.toDto
import com.example.agrogestion.data.remote.toEntity
import com.example.agrogestion.model.CatalogoAgronomico
import com.example.agrogestion.model.PredioInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AgronomicoRepository(
    private val dao: RegistroDao,
    private val api: AgroApiService = RetrofitClient.agroApi,
    private val catalogoApi: CatalogoApiService = RetrofitClient.catalogoApi,
    private val climaApi: ClimaApiService = RetrofitClient.climaApi
) {

    val registros: Flow<List<RegistroEntity>> = dao.obtenerTodos()

    suspend fun obtenerClimaPredio(latitud: Double, longitud: Double): Result<ClimaActualDto> =
        runCatching {
            climaApi.obtenerClimaActual(latitud, longitud).current
                ?: error("Respuesta climática vacía")
        }

    suspend fun obtenerPrediosCatalogo(): Result<List<PredioInfo>> =
        runCatching {
            val response = catalogoApi.obtenerPredios()
            response.map {
                PredioInfo(
                    id = it["id"] as String,
                    nombre = it["nombre"] as String,
                    ubicacion = it["ubicacion"] as String,
                    cultivo = it["cultivo"] as String,
                    superficieHa = (it["superficieHa"] as Double),
                    latitudBase = -32.882, // Valores mockeados por defecto ya que no vienen del backend
                    longitudBase = -71.248,
                    cuartelPrincipal = "Cuartel Central",
                    sistemaRiego = "Riego por Goteo (Autocompensado)",
                    etapaActual = "Crecimiento",
                    subEtapaDetalle = "Desarrollo",
                    vigorEstado = "Estable",
                    proximoFertirriegoHora = "06:00",
                    proximoFertirriegoSector = "Sector 1",
                    laminaRiegoM3Ha = 45.0
                )
            }
        }

    suspend fun refrescarDesdeServidor(): Boolean =
        runCatching {
            val remotos = api.obtenerRegistros()
            remotos.forEach { dao.insertar(it.toEntity()) }
            true
        }.getOrElse { false }

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
        val idLocal = dao.insertar(conCalculos)
        val result = runCatching { api.crearRegistro(conCalculos.copy(id = idLocal).toDto()) }
        if (result.isSuccess) {
            val remoteId = result.getOrNull()?.id
            if (remoteId != null) {
                dao.marcarSincronizado(idLocal, remoteId)
            }
        }
        return idLocal
    }

    suspend fun actualizarObservaciones(id: Long, texto: String) {
        val limpio = texto.trim()
        dao.actualizarObservaciones(id, limpio)
        runCatching {
            val actual = dao.obtenerTodos().first().find { it.id == id } ?: return
            if (actual.remoteId != null) {
                api.actualizarRegistro(actual.remoteId, actual.copy(observacionesFoliares = limpio).toDto())
            }
        }
    }

    suspend fun eliminarRegistro(id: Long) {
        val actual = dao.obtenerTodos().first().find { it.id == id }
        dao.eliminarPorId(id)
        if (actual?.remoteId != null) {
            runCatching { api.eliminarRegistro(actual.remoteId) }
        }
    }

    suspend fun sincronizarPendientes(): Boolean {
        val pendientes = dao.obtenerTodos().first().filter { !it.sincronizado }
        if (pendientes.isEmpty()) return true
        
        return runCatching {
            val response = api.sincronizarLote(pendientes.map { it.toDto() })
            // Asumimos que la API nos devuelve la lista con los remoteIds
            response.forEach { rem ->
                if (rem.localId != null && rem.id != null) {
                    dao.marcarSincronizado(rem.localId, rem.id)
                }
            }
            true
        }.getOrElse { throw it } // Lanza el error para que el ViewModel lo atrape y muestre
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