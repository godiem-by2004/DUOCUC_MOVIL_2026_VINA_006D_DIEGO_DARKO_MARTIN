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
}