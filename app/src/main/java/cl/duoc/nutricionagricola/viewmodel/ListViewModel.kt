package cl.duoc.nutricionagricola.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.nutricionagricola.data.DatabaseHelper
import cl.duoc.nutricionagricola.data.SampleEntity
import kotlinx.coroutines.flow.StateFlow

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)
    
    val samplesFlow: StateFlow<List<SampleEntity>> = dbHelper.samplesFlow

}