package com.example.agrogestion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.agrogestion.data.local.RegistroEntity
import com.example.agrogestion.data.local.UserDataStore
import com.example.agrogestion.model.CatalogoAgronomico
import com.example.agrogestion.model.PredioInfo
import com.example.agrogestion.repository.AgronomicoRepository
import com.example.agrogestion.util.FotoFoliarStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AgroViewModel(
    private val repository: AgronomicoRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _dashboardState = MutableStateFlow(AgroDashboardUiState())
    val dashboardState: StateFlow<AgroDashboardUiState> = _dashboardState.asStateFlow()

    private val _formState = MutableStateFlow(FormularioUiState())
    val formState: StateFlow<FormularioUiState> = _formState.asStateFlow()

    init {
        viewModelScope.launch { repository.inicializarSemillaSiVacio() }
        viewModelScope.launch {
            repository.registros.collectLatest { lista ->
                _dashboardState.update { it.copy(registros = lista) }
            }
        }
        viewModelScope.launch {
            userDataStore.nombreAsesor.collectLatest { nombre ->
                _dashboardState.update { it.copy(nombreAsesor = nombre) }
            }
        }
        viewModelScope.launch {
            userDataStore.predioActivoId.collectLatest { id ->
                val encontrado = CatalogoAgronomico.prediosDisponibles.find { it.id == id }
                    ?: CatalogoAgronomico.prediosDisponibles.first()
                aplicarPredio(encontrado)
            }
        }
    }

    fun seleccionarPredio(predio: PredioInfo) {
        aplicarPredio(predio)
        viewModelScope.launch { userDataStore.guardarPredioActivo(predio.id) }
    }

    private fun aplicarPredio(predio: PredioInfo) {
        _dashboardState.update { it.copy(predioSeleccionado = predio) }
        _formState.update {
            it.copy(
                predio = predio.nombre,
                ubicacion = predio.ubicacion,
                cultivo = predio.cultivo,
                etapa = predio.etapaActual,
                latitud = predio.latitudBase,
                longitud = predio.longitudBase,
                precisionMetros = null,
                gpsEstado = EstadoGps.REFERENCIA_PREDIO
            )
        }
    }

    fun actualizarFiltroHistorial(query: String) {
        _dashboardState.update { it.copy(filtroHistorial = query) }
    }

    fun actualizarNombreAsesor(nuevoNombre: String) {
        if (nuevoNombre.isNotBlank()) {
            viewModelScope.launch { userDataStore.guardarNombreAsesor(nuevoNombre.trim()) }
        }
    }

    private fun editar(campo: String?, cambio: (FormularioUiState) -> FormularioUiState) {
        _formState.update {
            val nuevo = cambio(it)
            nuevo.copy(
                camposTocados = if (campo != null) nuevo.camposTocados + campo else nuevo.camposTocados,
                guardadoExitoso = false
            )
        }
    }

    fun onPredioFormChange(predioNombre: String) {
        val info = CatalogoAgronomico.prediosDisponibles.find { it.nombre == predioNombre }
        editar(null) {
            it.copy(
                predio = predioNombre,
                ubicacion = info?.ubicacion ?: it.ubicacion,
                cultivo = info?.cultivo ?: it.cultivo,
                etapa = info?.etapaActual ?: it.etapa,
                latitud = info?.latitudBase ?: it.latitud,
                longitud = info?.longitudBase ?: it.longitud,
                precisionMetros = null,
                gpsEstado = EstadoGps.REFERENCIA_PREDIO
            )
        }
    }

    fun onCuartelChange(valor: String) = editar(Campo.CUARTEL) { it.copy(cuartel = valor) }
    fun onCultivoChange(valor: String) = editar(null) { it.copy(cultivo = valor) }
    fun onEtapaChange(valor: String) = editar(null) { it.copy(etapa = valor) }
    fun onPhChange(valor: String) = editar(Campo.PH) { it.copy(ph = valor) }
    fun onEcChange(valor: String) = editar(Campo.EC) { it.copy(ec = valor) }
    fun onNitrogenoChange(valor: String) = editar(Campo.N) { it.copy(nitrogeno = valor) }
    fun onFosforoChange(valor: String) = editar(Campo.P) { it.copy(fosforo = valor) }
    fun onPotasioChange(valor: String) = editar(Campo.K) { it.copy(potasio = valor) }
    fun onCalcioChange(valor: String) = editar(Campo.CA) { it.copy(calcio = valor) }
    fun onMagnesioChange(valor: String) = editar(Campo.MG) { it.copy(magnesio = valor) }
    fun onObservacionesChange(valor: String) =
        editar(Campo.OBSERVACIONES) { it.copy(observacionesFoliares = valor) }

    fun onGpsBuscando() {
        _formState.update { it.copy(gpsEstado = EstadoGps.BUSCANDO, mensajeGps = null) }
    }

    fun onGpsObtenido(lat: Double, lon: Double, precisionMetros: Float?) {
        _formState.update {
            it.copy(
                latitud = kotlin.math.round(lat * 1_000_000.0) / 1_000_000.0,
                longitud = kotlin.math.round(lon * 1_000_000.0) / 1_000_000.0,
                precisionMetros = precisionMetros,
                gpsEstado = EstadoGps.SENSOR,
                mensajeGps = null
            )
        }
    }

    fun onGpsError(mensaje: String) {
        _formState.update { it.copy(gpsEstado = EstadoGps.ERROR, mensajeGps = mensaje) }
    }

    fun onFotoCapturada(ruta: String) {
        FotoFoliarStorage.descartar(_formState.value.fotoUri.takeIf { it != ruta })
        _formState.update { it.copy(fotoUri = ruta, mensajeCamara = null, guardadoExitoso = false) }
    }

    fun onFotoQuitada() {
        FotoFoliarStorage.descartar(_formState.value.fotoUri)
        _formState.update { it.copy(fotoUri = null) }
    }

    fun onMensajeCamara(mensaje: String?) {
        _formState.update { it.copy(mensajeCamara = mensaje) }
    }

    fun cargarPresetOptimo() {
        val req = CatalogoAgronomico.obtenerRequerimiento(_formState.value.cultivo, _formState.value.etapa)
        _formState.update {
            val cuartelSugerido = CatalogoAgronomico.prediosDisponibles
                .find { p -> p.nombre == it.predio }?.cuartelPrincipal.orEmpty()
            it.copy(
                cuartel = it.cuartel.ifBlank { cuartelSugerido },
                ph = req.phOptimoRiego.toString(),
                ec = req.ceProyectada.toString(),
                nitrogeno = req.nRequerido.toInt().toString(),
                fosforo = req.pRequerido.toInt().toString(),
                potasio = req.kRequerido.toInt().toString(),
                calcio = req.caRequerido.toInt().toString(),
                magnesio = req.mgRequerido.toInt().toString(),
                observacionesFoliares = "Muestra foliar balanceada según requerimiento fenológico.",
                camposTocados = it.camposTocados + setOf(
                    Campo.CUARTEL, Campo.PH, Campo.EC, Campo.N, Campo.P, Campo.K, Campo.CA, Campo.MG, Campo.OBSERVACIONES
                ),
                mostrarErroresForzados = false,
                guardadoExitoso = false
            )
        }
    }

    fun guardarRegistroEnRoom(onSuccess: () -> Unit = {}) {
        val actual = _formState.value
        if (actual.guardando) return
        if (!actual.formularioCompletoValido) {
            _formState.update { it.copy(mostrarErroresForzados = true, guardadoExitoso = false) }
            return
        }

        fun num(texto: String): Double = FormularioNutricionalValidator.parsearDecimal(texto)!!

        _formState.update { it.copy(guardando = true) }

        viewModelScope.launch {
            try {
                delay(500)
                repository.guardarMuestra(
                    RegistroEntity(
                        predio = actual.predio,
                        ubicacion = actual.ubicacion,
                        cuartel = actual.cuartel.trim(),
                        cultivo = actual.cultivo,
                        etapa = actual.etapa,
                        ph = num(actual.ph),
                        ec = num(actual.ec),
                        nitrogeno = num(actual.nitrogeno),
                        fosforo = num(actual.fosforo),
                        potasio = num(actual.potasio),
                        calcio = num(actual.calcio),
                        magnesio = num(actual.magnesio),
                        latitud = actual.latitud,
                        longitud = actual.longitud,
                        precisionMetros = actual.precisionMetros,
                        gpsDesdeSensor = actual.gpsEstado == EstadoGps.SENSOR,
                        fotoUri = actual.fotoUri,
                        observacionesFoliares = actual.observacionesFoliares.trim(),
                        fechaMillis = System.currentTimeMillis()
                    )
                )
                _formState.update {
                    FormularioUiState(
                        predio = it.predio,
                        ubicacion = it.ubicacion,
                        cultivo = it.cultivo,
                        etapa = it.etapa,
                        latitud = it.latitud,
                        longitud = it.longitud,
                        precisionMetros = it.precisionMetros,
                        gpsEstado = it.gpsEstado,
                        guardadoExitoso = true
                    )
                }
                onSuccess()
            } finally {
                _formState.update { it.copy(guardando = false) }
            }
        }
    }

    fun actualizarObservacionesRegistro(id: Long, texto: String) {
        if (texto.trim().length < 5) return
        viewModelScope.launch { repository.actualizarObservaciones(id, texto) }
    }

    fun eliminarRegistro(id: Long) {
        val foto = _dashboardState.value.registros.firstOrNull { it.id == id }?.fotoUri
        viewModelScope.launch {
            repository.eliminarRegistro(id)
            FotoFoliarStorage.descartar(foto)
        }
    }

    fun sincronizarConServidorCentral() {
        if (_dashboardState.value.sincronizando) return
        viewModelScope.launch {
            _dashboardState.update {
                it.copy(
                    sincronizando = true,
                    mensajeSincronizacion = "Sincronizando registros locales (simulado)..."
                )
            }
            delay(1200)
            repository.sincronizarPendientes()
            _dashboardState.update {
                it.copy(
                    sincronizando = false,
                    mensajeSincronizacion = "Sincronización simulada completada."
                )
            }
            delay(3500)
            _dashboardState.update { it.copy(mensajeSincronizacion = null) }
        }
    }

    companion object {
        fun provideFactory(
            repository: AgronomicoRepository,
            userDataStore: UserDataStore
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AgroViewModel(repository, userDataStore) as T
            }
        }
    }
}
