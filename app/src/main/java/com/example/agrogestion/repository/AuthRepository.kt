package com.example.agrogestion.repository

import com.example.agrogestion.data.local.UserDataStore
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val userDataStore: UserDataStore) {

    companion object {
        const val CORREO_DEMO = "asesor@agrogestion.cl"
        const val CLAVE_DEMO = "Agro2026"
    }

    val sesionActiva: Flow<Boolean> = userDataStore.sesionActiva

    suspend fun iniciarSesion(correo: String, clave: String): Boolean {
        val correoLimpio = correo.trim()
        val valido = correoLimpio.equals(CORREO_DEMO, ignoreCase = true) && clave == CLAVE_DEMO
        if (valido) userDataStore.guardarSesion(correoLimpio.lowercase())
        return valido
    }

    suspend fun cerrarSesion() {
        userDataStore.cerrarSesion()
    }
}
