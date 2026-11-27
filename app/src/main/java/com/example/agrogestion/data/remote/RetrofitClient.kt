package com.example.agrogestion.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface AgroApiService {
    @GET("api/registros")
    suspend fun obtenerRegistros(): List<RegistroDto>

    @POST("api/registros")
    suspend fun crearRegistro(@Body registro: RegistroDto): RegistroDto

    @POST("api/registros/sync")
    suspend fun sincronizarLote(@Body registros: List<RegistroDto>): List<RegistroDto>

    @PUT("api/registros/{id}")
    suspend fun actualizarRegistro(
        @Path("id") id: Long,
        @Body registro: RegistroDto
    ): RegistroDto

    @DELETE("api/registros/{id}")
    suspend fun eliminarRegistro(@Path("id") id: Long): Response<Unit>
}

interface CatalogoApiService {
    @GET("api/catalogo/predios")
    suspend fun obtenerPredios(): List<Map<String, Any>>
}

interface ClimaApiService {
    @GET("v1/forecast")
    suspend fun obtenerClimaActual(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,wind_speed_10m,precipitation"
    ): OpenMeteoResponse
}

object RetrofitClient {
    private const val BASE_URL_BACKEND = "http://10.0.2.2:8081/"
    private const val BASE_URL_CATALOGO = "http://10.0.2.2:8082/"
    private const val BASE_URL_CLIMA = "https://api.open-meteo.com/"

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    val agroApi: AgroApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_BACKEND)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AgroApiService::class.java)
    }

    val catalogoApi: CatalogoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CATALOGO)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatalogoApiService::class.java)
    }

    val climaApi: ClimaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CLIMA)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClimaApiService::class.java)
    }
}