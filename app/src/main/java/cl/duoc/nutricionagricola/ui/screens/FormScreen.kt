package cl.duoc.nutricionagricola.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.nutricionagricola.util.CameraUtils
import cl.duoc.nutricionagricola.util.LocationUtils
import cl.duoc.nutricionagricola.viewmodel.FormViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    onNavigateBack: () -> Unit,
    viewModel: FormViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Camera Launcher
    var tempFile: File? = null
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempFile != null) {
                viewModel.onPhotoTaken(tempFile!!.absolutePath)
            }
        }
    )

    // Permission Launcher for Camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val (file, uri) = CameraUtils.createImageFileAndUri(context)
            tempFile = file
            cameraLauncher.launch(uri)
        }
    }

    // Permission Launcher for Location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            LocationUtils.getCurrentLocation(context,
                onLocationReceived = { lat, lon -> viewModel.onLocationUpdated(lat, lon) },
                onError = { /* Handle error if needed */ }
            )
        }
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            viewModel.resetSaveState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Registro") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Predio Name
            OutlinedTextField(
                value = state.farmName,
                onValueChange = { viewModel.onFarmNameChanged(it) },
                label = { Text("Nombre del Predio") },
                isError = state.farmNameError != null,
                trailingIcon = {
                    if (state.farmNameError != null) {
                        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = state.farmNameError != null) {
                Text(
                    text = state.farmNameError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Crop Type
            OutlinedTextField(
                value = state.cropType,
                onValueChange = { viewModel.onCropTypeChanged(it) },
                label = { Text("Tipo de Cultivo (Ej. Palto, Tomate)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Phenological Stage
            OutlinedTextField(
                value = state.phenologicalStage,
                onValueChange = { viewModel.onPhenologicalStageChanged(it) },
                label = { Text("Estado Fenológico") },
                modifier = Modifier.fillMaxWidth()
            )

            // Soil pH
            OutlinedTextField(
                value = state.soilPh,
                onValueChange = { viewModel.onSoilPhChanged(it) },
                label = { Text("pH del Suelo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.soilPhError != null,
                trailingIcon = {
                    if (state.soilPhError != null) {
                        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = state.soilPhError != null) {
                Text(
                    text = state.soilPhError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Soil CE
            OutlinedTextField(
                value = state.soilCe,
                onValueChange = { viewModel.onSoilCeChanged(it) },
                label = { Text("Conductividad Eléctrica (CE)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.soilCeError != null,
                trailingIcon = {
                    if (state.soilCeError != null) {
                        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = state.soilCeError != null) {
                Text(
                    text = state.soilCeError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Native Resources Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null)
                    Spacer(Modifier.padding(4.dp))
                    Text(if (state.latitude != null) "Ubicación ✔" else "GPS")
                }

                Button(onClick = {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null)
                    Spacer(Modifier.padding(4.dp))
                    Text(if (state.photoUri != null) "Foto ✔" else "Cámara")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit
            Button(
                onClick = { viewModel.saveSample() },
                enabled = state.isFormValid,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Guardar Registro")
            }
        }
    }
}