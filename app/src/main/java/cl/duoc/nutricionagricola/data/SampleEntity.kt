package cl.duoc.nutricionagricola.data

data class SampleEntity(
    val id: Int = -1,
    val farmName: String,
    val cropType: String,
    val phenologicalStage: String,
    val soilPh: Double,
    val soilCe: Double,
    val latitude: Double?,
    val longitude: Double?,
    val photoUri: String?
)