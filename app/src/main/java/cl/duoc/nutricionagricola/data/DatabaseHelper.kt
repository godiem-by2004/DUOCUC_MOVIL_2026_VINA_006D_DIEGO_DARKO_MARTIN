package cl.duoc.nutricionagricola.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val _samplesFlow = MutableStateFlow<List<SampleEntity>>(emptyList())
    val samplesFlow: StateFlow<List<SampleEntity>> = _samplesFlow

    init {
        // Load initial data
        refreshSamples()
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("""
            CREATE TABLE $TABLE_SAMPLES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FARM_NAME TEXT NOT NULL,
                $COLUMN_CROP_TYPE TEXT NOT NULL,
                $COLUMN_STAGE TEXT NOT NULL,
                $COLUMN_PH REAL NOT NULL,
                $COLUMN_CE REAL NOT NULL,
                $COLUMN_LAT REAL,
                $COLUMN_LON REAL,
                $COLUMN_PHOTO_URI TEXT
            )
        """)
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SAMPLES")
        onCreate(db)
    }

    fun insertSample(sample: SampleEntity): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FARM_NAME, sample.farmName)
            put(COLUMN_CROP_TYPE, sample.cropType)
            put(COLUMN_STAGE, sample.phenologicalStage)
            put(COLUMN_PH, sample.soilPh)
            put(COLUMN_CE, sample.soilCe)
            put(COLUMN_LAT, sample.latitude)
            put(COLUMN_LON, sample.longitude)
            put(COLUMN_PHOTO_URI, sample.photoUri)
        }
        val id = db.insert(TABLE_SAMPLES, null, values)
        db.close()
        refreshSamples() // update flow
        return id
    }

    private fun refreshSamples() {
        val samplesList = mutableListOf<SampleEntity>()
        val selectQuery = "SELECT * FROM $TABLE_SAMPLES ORDER BY $COLUMN_ID DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val sample = SampleEntity(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    farmName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FARM_NAME)),
                    cropType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CROP_TYPE)),
                    phenologicalStage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STAGE)),
                    soilPh = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PH)),
                    soilCe = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CE)),
                    latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LAT)),
                    longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LON)),
                    photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI))
                )
                samplesList.add(sample)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        _samplesFlow.value = samplesList
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "nutricion_agricola.db"
        const val TABLE_SAMPLES = "samples"
        const val COLUMN_ID = "id"
        const val COLUMN_FARM_NAME = "farm_name"
        const val COLUMN_CROP_TYPE = "crop_type"
        const val COLUMN_STAGE = "phenological_stage"
        const val COLUMN_PH = "soil_ph"
        const val COLUMN_CE = "soil_ce"
        const val COLUMN_LAT = "latitude"
        const val COLUMN_LON = "longitude"
        const val COLUMN_PHOTO_URI = "photo_uri"
    }
}