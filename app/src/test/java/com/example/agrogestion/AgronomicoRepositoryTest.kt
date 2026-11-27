package com.example.agrogestion

import com.example.agrogestion.data.local.RegistroDao
import com.example.agrogestion.data.local.RegistroEntity
import com.example.agrogestion.data.remote.AgroApiService
import com.example.agrogestion.data.remote.ClimaActualDto
import com.example.agrogestion.data.remote.ClimaApiService
import com.example.agrogestion.data.remote.OpenMeteoResponse
import com.example.agrogestion.data.remote.toDto
import com.example.agrogestion.repository.AgronomicoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AgronomicoRepositoryTest {

    private lateinit var dao: RegistroDao
    private lateinit var agroApi: AgroApiService
    private lateinit var climaApi: ClimaApiService
    private lateinit var repository: AgronomicoRepository

    private val muestraBase = RegistroEntity(
        id = 1L,
        predio = "Fundo San Isidro",
        ubicacion = "Quillota",
        cuartel = "Cuartel 4",
        cultivo = "Palto Hass",
        etapa = "Engorde de Fruto",
        ph = 6.5,
        ec = 1.5,
        nitrogeno = 100.0,
        fosforo = 45.0,
        potasio = 160.0,
        calcio = 60.0,
        magnesio = 25.0,
        latitud = -32.88,
        longitud = -71.24,
        observacionesFoliares = "Muestra de prueba",
        fechaMillis = 1700000000L,
        sincronizado = false
    )

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        agroApi = mockk(relaxed = true)
        climaApi = mockk(relaxed = true)
        every { dao.obtenerTodos() } returns flowOf(listOf(muestraBase))
        repository = AgronomicoRepository(dao, agroApi, climaApi)
    }

    @Test
    fun `inicializarSemillaSiVacio inserta 4 registros cuando la tabla esta vacia`() = runTest {
        coEvery { dao.contarRegistros() } returns 0
        coEvery { dao.insertar(any()) } returns 1L

        repository.inicializarSemillaSiVacio()

        coVerify(exactly = 4) { dao.insertar(any()) }
    }

    @Test
    fun `inicializarSemillaSiVacio no inserta si ya existen registros`() = runTest {
        coEvery { dao.contarRegistros() } returns 3

        repository.inicializarSemillaSiVacio()

        coVerify(exactly = 0) { dao.insertar(any()) }
    }

    @Test
    fun `guardarMuestra calcula recomendacion y sincroniza cuando API responde ok`() = runTest {
        coEvery { dao.insertar(any()) } returns 10L
        coEvery { agroApi.crearRegistro(any()) } returns muestraBase.toDto()

        val id = repository.guardarMuestra(muestraBase)

        assertEquals(10L, id)
        coVerify {
            dao.insertar(match { it.recomendacion.contains("+20 kg/ha N") && it.recomendacion.contains("+20 kg/ha K₂O") })
            dao.marcarTodosSincronizados()
        }
    }

    @Test
    fun `guardarMuestra mantiene respaldo local si el microservicio falla`() = runTest {
        coEvery { dao.insertar(any()) } returns 11L
        coEvery { agroApi.crearRegistro(any()) } throws RuntimeException("Timeout")

        val id = repository.guardarMuestra(muestraBase.copy(nitrogeno = 120.0, potasio = 180.0))

        assertEquals(11L, id)
        coVerify(exactly = 0) { dao.marcarTodosSincronizados() }
    }

    @Test
    fun `sincronizarPendientes envia lote y marca sincronizados`() = runTest {
        coEvery { agroApi.sincronizarLote(any()) } returns listOf(muestraBase.toDto())

        val resultado = repository.sincronizarPendientes()

        assertTrue(resultado)
        coVerify {
            agroApi.sincronizarLote(match { it.size == 1 })
            dao.marcarTodosSincronizados()
        }
    }

    @Test
    fun `sincronizarPendientes retorna false ante error de red`() = runTest {
        coEvery { agroApi.sincronizarLote(any()) } throws RuntimeException("Sin red")

        val resultado = repository.sincronizarPendientes()

        assertFalse(resultado)
    }

    @Test
    fun `actualizarObservaciones y eliminarRegistro ejecutan DAO y API`() = runTest {
        coEvery { agroApi.actualizarRegistro(any(), any()) } returns muestraBase.toDto()
        coEvery { agroApi.eliminarRegistro(any()) } returns Response.success(Unit)

        repository.actualizarObservaciones(1L, "Nueva nota foliar")
        repository.eliminarRegistro(1L)

        coVerify {
            dao.actualizarObservaciones(1L, "Nueva nota foliar")
            agroApi.actualizarRegistro(1L, any())
            dao.eliminarPorId(1L)
            agroApi.eliminarRegistro(1L)
        }
    }

    @Test
    fun `obtenerClimaPredio retorna datos de OpenMeteo correctamente`() = runTest {
        val dto = ClimaActualDto(22.4, 58, 12.5, 0.0)
        coEvery { climaApi.obtenerClimaActual(any(), any(), any()) } returns OpenMeteoResponse(dto)

        val result = repository.obtenerClimaPredio(-32.88, -71.24)

        assertTrue(result.isSuccess)
        assertEquals(22.4, result.getOrNull()?.temperaturaCelsius ?: 0.0, 0.01)
    }
}