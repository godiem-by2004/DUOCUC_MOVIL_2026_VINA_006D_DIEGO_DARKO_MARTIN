package com.example.agrogestion.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "agronomico_prefs")

class UserDataStore(private val context: Context) {
    companion object {
        val NOMBRE_ASESOR = stringPreferencesKey("nombre_asesor")
        val PREDIO_ACTIVO_ID = stringPreferencesKey("predio_activo_id")
        val SESION_ACTIVA = booleanPreferencesKey("sesion_activa")
        val CORREO_SESION = stringPreferencesKey("correo_sesion")
    }

    val nombreAsesor: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[NOMBRE_ASESOR] ?: "Ing. Agrónomo en Turno"
    }

    val predioActivoId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PREDIO_ACTIVO_ID] ?: "san_isidro"
    }

    val sesionActiva: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[SESION_ACTIVA] ?: false
    }

    suspend fun guardarNombreAsesor(nombre: String) {
        context.dataStore.edit { prefs ->
            prefs[NOMBRE_ASESOR] = nombre
        }
    }

    suspend fun guardarPredioActivo(predioId: String) {
        context.dataStore.edit { prefs ->
            prefs[PREDIO_ACTIVO_ID] = predioId
        }
    }

    suspend fun guardarSesion(correo: String) {
        context.dataStore.edit { prefs ->
            prefs[SESION_ACTIVA] = true
            prefs[CORREO_SESION] = correo
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { prefs ->
            prefs[SESION_ACTIVA] = false
            prefs.remove(CORREO_SESION)
        }
    }
}
