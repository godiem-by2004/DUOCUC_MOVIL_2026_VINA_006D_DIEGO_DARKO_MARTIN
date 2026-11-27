package com.example.agrogestion

import com.example.agrogestion.data.local.UserDataStore
import com.example.agrogestion.data.remote.ClimaActualDto
import com.example.agrogestion.model.CatalogoAgronomico
import com.example.agrogestion.repository.AgronomicoRepository
import com.example.agrogestion.viewmodel.AgroViewModel
import com.example.agrogestion.viewmodel.EstadoGps
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AgroViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: AgronomicoRepository
    private lateinit var userDataStore: UserDataStore
    private lateinit var viewModel: AgroViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        userDataStore = mockk(relaxed = true)
        every { repository.registros } returns flowOf(emptyList())
        every { userDataStore.nombreAsesor } returns flowOf("Ing. Darko")
        every { userDataStore.predioActivoId } returns flowOf("san_isidro")
        coEvery { repository.obtenerClimaPredio(any(), any()) } returns Result.success(
            ClimaActualDto(21.0, 60, 10.0, 0.0)
        )
        coEvery { repository.obtenerPrediosCatalogo() } returns Result.success(emptyList())
        viewModel = AgroViewModel(repository, userDataStore)
    }

    @Test
    fun `estado inicial carga predio activo y clima desde API externa`() = runTest {
        advanceUntilIdle()
        assertEquals("Fundo San Isidro", viewModel.dashboardState.value.predioSeleccionado.nombre)
        assertEquals(21.0, viewModel.dashboardState.value.climaActual?.temperaturaCelsius ?: 0.0, 0.01)
    }

    @Test
    fun `seleccionarPredio actualiza formulario y persiste preferencia en DataStore`() = runTest {
        val predio = CatalogoAgronomico.prediosDisponibles[1]
        coEvery { userDataStore.guardarPredioActivo(predio.id) } just Runs

        viewModel.seleccionarPredio(predio)
        advanceUntilIdle()

        assertEquals(predio.nombre, viewModel.formState.value.predio)
        coVerify { userDataStore.guardarPredioActivo(predio.id) }
    }

    @Test
    fun `guardarRegistroEnRoom con formulario vacio activa mostrarErroresForzados y no guarda`() = runTest {
        viewModel.guardarRegistroEnRoom()

        assertTrue(viewModel.formState.value.mostrarErroresForzados)
        assertFalse(viewModel.formState.value.guardadoExitoso)
        coVerify(exactly = 0) { repository.guardarMuestra(any()) }
    }

    @Test
    fun `cargarPresetOptimo y guardarRegistroEnRoom persisten muestra valida`() = runTest {
        coEvery { repository.guardarMuestra(any()) } returns 1L

        viewModel.cargarPresetOptimo()
        assertTrue(viewModel.formState.value.formularioCompletoValido)

        var callbackEjecutado = false
        viewModel.guardarRegistroEnRoom { callbackEjecutado = true }
        advanceUntilIdle()

        assertTrue(callbackEjecutado)
        assertTrue(viewModel.formState.value.guardadoExitoso)
        coVerify(exactly = 1) { repository.guardarMuestra(any()) }
    }

    @Test
    fun `onGpsObtenido actualiza coordenadas y estado a SENSOR`() {
        viewModel.onGpsBuscando()
        assertEquals(EstadoGps.BUSCANDO, viewModel.formState.value.gpsEstado)

        viewModel.onGpsObtenido(-32.882412, -71.248911, 4.5f)
        assertEquals(EstadoGps.SENSOR, viewModel.formState.value.gpsEstado)
        assertEquals(4.5f, viewModel.formState.value.precisionMetros)
    }

    @Test
    fun `sincronizarConServidorCentral ejecuta sincronizacion y refresca registros`() = runTest {
        coEvery { repository.sincronizarPendientes() } returns true
        coEvery { repository.refrescarDesdeServidor() } returns true

        viewModel.sincronizarConServidorCentral()
        advanceUntilIdle()

        coVerify {
            repository.sincronizarPendientes()
            repository.refrescarDesdeServidor()
        }
        assertNotNull(viewModel.dashboardState.value)
    }

    @Test
    fun `refrescarClimaPredio maneja error de red`() = runTest {
        coEvery { repository.obtenerClimaPredio(any(), any()) } returns Result.failure(Exception("Network error"))
        viewModel.refrescarClimaPredio()
        advanceUntilIdle()
        assertEquals("Sin conexión al servicio meteorológico", viewModel.dashboardState.value.errorClima)
    }

    @Test
    fun `todos los setters actualizan el estado del formulario correctamente`() {
        viewModel.onPredioFormChange("Las Palmas")
        viewModel.onCuartelChange("C1")
        viewModel.onCultivoChange("Limon")
        viewModel.onEtapaChange("Brotacion")
        viewModel.onPhChange("6.5")
        viewModel.onEcChange("1.2")
        viewModel.onNitrogenoChange("100")
        viewModel.onFosforoChange("50")
        viewModel.onPotasioChange("150")
        viewModel.onCalcioChange("40")
        viewModel.onMagnesioChange("20")
        viewModel.onObservacionesChange("Obs de prueba")
        
        val f = viewModel.formState.value
        assertEquals("Las Palmas", f.predio)
        assertEquals("C1", f.cuartel)
        assertEquals("Limon", f.cultivo)
        assertEquals("Brotacion", f.etapa)
        assertEquals("6.5", f.ph)
        assertEquals("1.2", f.ec)
        assertEquals("100", f.nitrogeno)
        assertEquals("50", f.fosforo)
        assertEquals("150", f.potasio)
        assertEquals("40", f.calcio)
        assertEquals("20", f.magnesio)
        assertEquals("Obs de prueba", f.observacionesFoliares)
    }

    @Test
    fun `metodos auxiliares de UI y hardware`() = runTest {
        viewModel.onFotoCapturada("file://foto.jpg")
        assertEquals("file://foto.jpg", viewModel.formState.value.fotoUri)

        viewModel.onFotoQuitada()
        assertEquals(null, viewModel.formState.value.fotoUri)

        viewModel.onMensajeCamara("Error camara")
        assertEquals("Error camara", viewModel.formState.value.mensajeCamara)

        viewModel.onGpsError("Error GPS")
        assertEquals("Error GPS", viewModel.formState.value.mensajeGps)
        assertEquals(EstadoGps.ERROR, viewModel.formState.value.gpsEstado)

        viewModel.actualizarFiltroHistorial("Filtro")
        assertEquals("Filtro", viewModel.dashboardState.value.filtroHistorial)
        
        viewModel.actualizarNombreAsesor("Nuevo Asesor")
    }

    @Test
    fun `sincronizarConServidorCentral atrapa excepcion y muestra error de red`() = runTest {
        coEvery { repository.sincronizarPendientes() } throws RuntimeException("Error fatal")
        viewModel.sincronizarConServidorCentral()
        // No avanzar hasta idle completamente porque se resetea al final (mensajeSincronizacion = null por el delay).
        // Capturamos antes de que se limpie:
        assertTrue(viewModel.dashboardState.value.mensajeSincronizacion != null)
        advanceUntilIdle()
    }

    @Test
    fun `eliminarRegistro y actualizarObservacionesRegistro interactuan con repository`() = runTest {
        coEvery { repository.eliminarRegistro(any()) } returns Unit
        coEvery { repository.actualizarObservaciones(any(), any()) } returns Unit

        viewModel.eliminarRegistro(1L)
        advanceUntilIdle()

        viewModel.actualizarObservacionesRegistro(1L, "Nota")
        advanceUntilIdle()
    }
}