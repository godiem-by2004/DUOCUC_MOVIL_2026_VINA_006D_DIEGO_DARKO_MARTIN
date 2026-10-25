package cl.duoc.nutricionagricola.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.nutricionagricola.data.DatabaseHelper
import cl.duoc.nutricionagricola.data.SampleEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FormState(
    val farmName: String = "",
    val cropType: String = "",
    val phenologicalStage: String = "",
    val soilPh: String = "",
    val soilCe: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val photoUri: String? = null,
    
    // Validations
    val farmNameError: String? = null,
    val soilPhError: String? = null,
    val soilCeError: String? = null,
    val isFormValid: Boolean = false,
    val saveSuccess: Boolean = false
)

class FormViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val _uiState = MutableStateFlow(FormState())
    val uiState: StateFlow<FormState> = _uiState.asStateFlow()

    fun onFarmNameChanged(newValue: String) {
        val error = if (newValue.isBlank()) "El nombre del predio es obligatorio" else null
        _uiState.value = _uiState.value.copy(farmName = newValue, farmNameError = error)
        validateForm()
    }

    fun onCropTypeChanged(newValue: String) {
        _uiState.value = _uiState.value.copy(cropType = newValue)
        validateForm()
    }

    fun onPhenologicalStageChanged(newValue: String) {
        _uiState.value = _uiState.value.copy(phenologicalStage = newValue)
        validateForm()
    }

    fun onSoilPhChanged(newValue: String) {
        val parsed = newValue.toDoubleOrNull()
        val error = if (parsed == null || parsed < 0.0 || parsed > 14.0) "El pH debe estar entre 0 y 14" else null
        _uiState.value = _uiState.value.copy(soilPh = newValue, soilPhError = error)
        validateForm()
    }

    fun onSoilCeChanged(newValue: String) {
        val parsed = newValue.toDoubleOrNull()
        val error = if (parsed == null || parsed < 0.0) "La CE debe ser un número positivo" else null
        _uiState.value = _uiState.value.copy(soilCe = newValue, soilCeError = error)
        validateForm()
    }

    fun onLocationUpdated(lat: Double, lon: Double) {
        _uiState.value = _uiState.value.copy(latitude = lat, longitude = lon)
    }

    fun onPhotoTaken(uri: String) {
        _uiState.value = _uiState.value.copy(photoUri = uri)
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.farmName.isNotBlank() &&
                state.cropType.isNotBlank() &&
                state.phenologicalStage.isNotBlank() &&
                state.soilPhError == null && state.soilPh.isNotBlank() &&
                state.soilCeError == null && state.soilCe.isNotBlank()

        _uiState.value = state.copy(isFormValid = isValid)
    }

    fun saveSample() {
        if (!_uiState.value.isFormValid) return

        val state = _uiState.value
        val entity = SampleEntity(
            farmName = state.farmName,
            cropType = state.cropType,
            phenologicalStage = state.phenologicalStage,
            soilPh = state.soilPh.toDouble(),
            soilCe = state.soilCe.toDouble(),
            latitude = state.latitude,
            longitude = state.longitude,
            photoUri = state.photoUri
        )

        dbHelper.insertSample(entity)
        _uiState.value = _uiState.value.copy(saveSuccess = true)
    }
    
    fun resetSaveState() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}