package com.example.agrogestion.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.agrogestion.ui.screens.FertirriegoScreen
import com.example.agrogestion.ui.screens.HistorialScreen
import com.example.agrogestion.ui.screens.LoginScreen
import com.example.agrogestion.ui.screens.NuevoRegistroScreen
import com.example.agrogestion.ui.screens.PrediosScreen
import com.example.agrogestion.ui.theme.AgroMintContainer
import com.example.agrogestion.ui.theme.AgroNeutralBg
import com.example.agrogestion.ui.theme.AgroPrimary
import com.example.agrogestion.ui.theme.AgroTextSecondary
import com.example.agrogestion.viewmodel.AgroViewModel
import com.example.agrogestion.viewmodel.LoginViewModel

sealed class DestinoAgro(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
) {
    object Login : DestinoAgro("login", "Ingresar", Icons.Default.Lock)
    object Predios : DestinoAgro("predios", "Predios", Icons.Default.GridView)
    object NuevoRegistro : DestinoAgro("nuevo_registro", "Nuevo Registro", Icons.Default.AddChart)
    object Fertirriego : DestinoAgro("fertirriego", "Fertirriego", Icons.Default.Science)
    object Historial : DestinoAgro("historial", "Historial", Icons.Default.History)
}

@Composable
fun AppNavigation(viewModel: AgroViewModel, loginViewModel: LoginViewModel) {
    val navController = rememberNavController()
    val dashboardState by viewModel.dashboardState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val loginState by loginViewModel.uiState.collectAsState()

    val sesionActiva by loginViewModel.sesionActiva.collectAsState(initial = null)

    var rutaInicial by rememberSaveable { mutableStateOf<String?>(null) }
    if (rutaInicial == null && sesionActiva != null) {
        rutaInicial = if (sesionActiva == true) DestinoAgro.Predios.ruta else DestinoAgro.Login.ruta
    }

    val inicio = rutaInicial
    if (inicio == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AgroNeutralBg),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AgroPrimary)
        }
        return
    }

    val itemsMenu = listOf(
        DestinoAgro.Predios,
        DestinoAgro.NuevoRegistro,
        DestinoAgro.Fertirriego,
        DestinoAgro.Historial
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val cerrarSesion: () -> Unit = {
        loginViewModel.cerrarSesion()
        navController.navigate(DestinoAgro.Login.ruta) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    Scaffold(
        bottomBar = {
            if (rutaActual != null && rutaActual != DestinoAgro.Login.ruta) {
                NavigationBar(
                    containerColor = Color.White
                ) {
                    itemsMenu.forEach { destino ->
                        val seleccionado = rutaActual == destino.ruta
                        NavigationBarItem(
                            selected = seleccionado,
                            onClick = { navegarDesdeMenu(navController, destino.ruta) },
                            icon = {
                                Icon(
                                    imageVector = destino.icono,
                                    contentDescription = destino.titulo
                                )
                            },
                            label = {
                                Text(
                                    text = destino.titulo,
                                    fontSize = 11.sp,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AgroPrimary,
                                selectedTextColor = AgroPrimary,
                                indicatorColor = AgroMintContainer,
                                unselectedIconColor = AgroTextSecondary,
                                unselectedTextColor = AgroTextSecondary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = inicio,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(250)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            composable(DestinoAgro.Login.ruta) {
                LoginScreen(
                    uiState = loginState,
                    onCorreoChange = loginViewModel::onCorreoChange,
                    onClaveChange = loginViewModel::onClaveChange,
                    onIngresarClick = {
                        loginViewModel.iniciarSesion {
                            navController.navigate(DestinoAgro.Predios.ruta) {
                                popUpTo(DestinoAgro.Login.ruta) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(DestinoAgro.Predios.ruta) {
                PrediosScreen(
                    uiState = dashboardState,
                    onPredioSelected = { viewModel.seleccionarPredio(it) },
                    onNavigateNuevoRegistro = { navegarDesdeMenu(navController, DestinoAgro.NuevoRegistro.ruta) },
                    onNavigateFertirriego = { navegarDesdeMenu(navController, DestinoAgro.Fertirriego.ruta) },
                    onSyncClick = { viewModel.sincronizarConServidorCentral() },
                    onCerrarSesion = cerrarSesion
                )
            }

            composable(DestinoAgro.NuevoRegistro.ruta) {
                NuevoRegistroScreen(
                    formState = formState,
                    pendientesRoom = dashboardState.pendientesSincronizar,
                    onPredioChange = viewModel::onPredioFormChange,
                    onCuartelChange = viewModel::onCuartelChange,
                    onCultivoChange = viewModel::onCultivoChange,
                    onEtapaChange = viewModel::onEtapaChange,
                    onPhChange = viewModel::onPhChange,
                    onEcChange = viewModel::onEcChange,
                    onNitrogenoChange = viewModel::onNitrogenoChange,
                    onFosforoChange = viewModel::onFosforoChange,
                    onPotasioChange = viewModel::onPotasioChange,
                    onCalcioChange = viewModel::onCalcioChange,
                    onMagnesioChange = viewModel::onMagnesioChange,
                    onObservacionesChange = viewModel::onObservacionesChange,
                    onGpsBuscando = viewModel::onGpsBuscando,
                    onGpsObtenido = viewModel::onGpsObtenido,
                    onGpsError = viewModel::onGpsError,
                    onFotoCapturada = viewModel::onFotoCapturada,
                    onFotoQuitada = viewModel::onFotoQuitada,
                    onMensajeCamara = viewModel::onMensajeCamara,
                    onCargarPresetOptimo = viewModel::cargarPresetOptimo,
                    onGuardarClick = { viewModel.guardarRegistroEnRoom() },
                    onSyncClick = { viewModel.sincronizarConServidorCentral() },
                    sincronizando = dashboardState.sincronizando,
                    mensajeSincronizacion = dashboardState.mensajeSincronizacion,
                    onCerrarSesion = cerrarSesion
                )
            }

            composable(DestinoAgro.Fertirriego.ruta) {
                FertirriegoScreen(
                    uiState = dashboardState,
                    onSyncClick = { viewModel.sincronizarConServidorCentral() },
                    onCerrarSesion = cerrarSesion
                )
            }

            composable(DestinoAgro.Historial.ruta) {
                HistorialScreen(
                    uiState = dashboardState,
                    onFiltroChange = viewModel::actualizarFiltroHistorial,
                    onNombreAsesorChange = viewModel::actualizarNombreAsesor,
                    onEliminarRegistro = viewModel::eliminarRegistro,
                    onActualizarObservaciones = viewModel::actualizarObservacionesRegistro,
                    onSyncClick = { viewModel.sincronizarConServidorCentral() },
                    onCerrarSesion = cerrarSesion
                )
            }
        }
    }
}

private fun navegarDesdeMenu(navController: NavHostController, ruta: String) {
    navController.navigate(ruta) {
        popUpTo(DestinoAgro.Predios.ruta) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
