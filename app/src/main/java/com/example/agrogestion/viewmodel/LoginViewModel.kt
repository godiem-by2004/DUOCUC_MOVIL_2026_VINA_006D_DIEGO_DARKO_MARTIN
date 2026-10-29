package com.example.agrogestion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.agrogestion.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object CampoLogin {
    const val CORREO = "correo"
    const val CLAVE = "clave"
}

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val camposTocados: Set<String> = emptySet(),
    val mostrarErroresForzados: Boolean = false,
    val cargando: Boolean = false,
    val errorCredenciales: String? = null
) {
    private val rawCorreo get() = LoginValidator.validarCorreo(correo)
    private val rawClave get() = LoginValidator.validarClave(clave)

    val formularioValido: Boolean
        get() = rawCorreo.esValido && rawClave.esValido

    private fun visible(campo: String, r: ValidacionCampo): ValidacionCampo =
        if (mostrarErroresForzados || campo in camposTocados) r else ValidacionCampo()

    val valCorreo get() = visible(CampoLogin.CORREO, rawCorreo)
    val valClave get() = visible(CampoLogin.CLAVE, rawClave)
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val sesionActiva: Flow<Boolean> = authRepository.sesionActiva

    fun onCorreoChange(valor: String) = _uiState.update {
        it.copy(correo = valor, camposTocados = it.camposTocados + CampoLogin.CORREO, errorCredenciales = null)
    }

    fun onClaveChange(valor: String) = _uiState.update {
        it.copy(clave = valor, camposTocados = it.camposTocados + CampoLogin.CLAVE, errorCredenciales = null)
    }

    fun iniciarSesion(onExito: () -> Unit) {
        val actual = _uiState.value
        if (actual.cargando) return
        if (!actual.formularioValido) {
            _uiState.update { it.copy(mostrarErroresForzados = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, errorCredenciales = null) }
            try {
                delay(800)
                if (authRepository.iniciarSesion(actual.correo, actual.clave)) {
                    _uiState.value = LoginUiState()
                    onExito()
                } else {
                    _uiState.update { it.copy(errorCredenciales = "Correo o contraseña incorrectos") }
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun cerrarSesion() {
        _uiState.value = LoginUiState()
        viewModelScope.launch { authRepository.cerrarSesion() }
    }

    companion object {
        fun provideFactory(authRepository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoginViewModel(authRepository) as T
                }
            }
    }
}
