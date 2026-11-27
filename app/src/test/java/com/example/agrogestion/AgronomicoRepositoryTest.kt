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
    private lateinit var catalogoApi: com.example.agrogestion.data.remote.CatalogoApiService
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
        catalogoApi = mockk(relaxed = true)
        every { dao.obtenerTodos() } returns flowOf(listOf(muestraBase))
        repository = AgronomicoRepository(dao, agroApi, catalogoApi, climaApi)
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
        }
    }

    @Test
    fun `guardarMuestra mantiene respaldo local si el microservicio falla`() = runTest {
        coEvery { dao.insertar(any()) } returns 11L
        coEvery { agroApi.crearRegistro(any()) } throws RuntimeException("Timeout")

        val id = repository.guardarMuestra(muestraBase.copy(nitrogeno = 120.0, potasio = 180.0))

        assertEquals(11L, id)
    }

    @Test
    fun `sincronizarPendientes envia lote y marca sincronizados`() = runTest {
        coEvery { agroApi.sincronizarLote(any()) } returns listOf(muestraBase.toDto())

        val resultado = repository.sincronizarPendientes()

        assertTrue(resultado)
        coVerify {
            agroApi.sincronizarLote(match { it.size == 1 })
        }
    }

    @Test
    fun `sincronizarPendientes lanza excepcion ante error de red`() = runTest {
        coEvery { agroApi.sincronizarLote(any()) } throws RuntimeException("Sin red")

        val result = runCatching { repository.sincronizarPendientes() }

        assertTrue(result.isFailure)
        assertEquals("Sin red", result.exceptionOrNull()?.message)
    }

    @Test
    fun `actualizarObservaciones y eliminarRegistro ejecutan DAO y API`() = runTest {
        coEvery { agroApi.actualizarRegistro(any(), any()) } returns muestraBase.toDto()
        coEvery { agroApi.eliminarRegistro(any()) } returns Response.success(Unit)

        repository.actualizarObservaciones(1L, "Nueva nota foliar")
        repository.eliminarRegistro(1L)

        coVerify {
            dao.actualizarObservaciones(1L, "Nueva nota foliar")
            dao.eliminarPorId(1L)
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

    @Test
    fun `obtenerPrediosCatalogo mapea correctamente y maneja errores`() = runTest {
        // Exito
        val mockData = listOf(mapOf<String, Any>("id" to "p1", "nombre" to "P1", "ubicacion" to "U1", "cultivo" to "C1", "superficieHa" to 10.0))
        coEvery { catalogoApi.obtenerPredios() } returns mockData
        val result = repository.obtenerPrediosCatalogo()
        assertTrue(result.isSuccess)
        assertEquals("p1", result.getOrNull()?.first()?.id)

        // Error
        coEvery { catalogoApi.obtenerPredios() } throws RuntimeException("Error catalogo")
        assertTrue(repository.obtenerPrediosCatalogo().isFailure)
    }

    @Test
    fun `refrescarDesdeServidor maneja exito y error`() = runTest {
        // Exito
        val dto = com.example.agrogestion.data.remote.RegistroDto(predio = "P1", ubicacion = "U1", cuartel = "C1", cultivo = "Cu1", etapa = "E1", ph = 6.0, ec = 1.0, nitrogeno = 1.0, fosforo = 1.0, potasio = 1.0, calcio = 1.0, magnesio = 1.0, latitud = 1.0, longitud = 1.0, observacionesFoliares = "obs", fechaMillis = 100)
        coEvery { agroApi.obtenerRegistros() } returns listOf(dto)
        assertTrue(repository.refrescarDesdeServidor())
        coVerify(exactly = 1) { dao.insertar(any()) }

        // Error
        coEvery { agroApi.obtenerRegistros() } throws RuntimeException("Error refrescar")
        assertFalse(repository.refrescarDesdeServidor())
    }

    @Test
    fun `obtenerClimaPredio falla si current es null`() = runTest {
        coEvery { climaApi.obtenerClimaActual(any(), any(), any()) } returns OpenMeteoResponse(null)
        val result = repository.obtenerClimaPredio(0.0, 0.0)
        assertTrue(result.isFailure)
    }

    @Test
    fun `sincronizarPendientes retorna true si no hay pendientes`() = runTest {
        val muestraSincronizada = muestraBase.copy(sincronizado = true)
        every { dao.obtenerTodos() } returns flowOf(listOf(muestraSincronizada))
        assertTrue(repository.sincronizarPendientes())
        coVerify(exactly = 0) { agroApi.sincronizarLote(any()) }
    }

    @Test
    fun `actualizarObservaciones y eliminarRegistro no llaman a la API si remoteId es null`() = runTest {
        val muestraSinRemoteId = muestraBase.copy(id = 2L, remoteId = null)
        every { dao.obtenerTodos() } returns flowOf(listOf(muestraSinRemoteId))
        
        repository.actualizarObservaciones(2L, "nota")
        repository.eliminarRegistro(2L)
        
        coVerify(exactly = 0) { agroApi.actualizarRegistro(any(), any()) }
        coVerify(exactly = 0) { agroApi.eliminarRegistro(any()) }
    }
}