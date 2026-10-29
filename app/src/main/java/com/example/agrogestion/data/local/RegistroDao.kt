package com.example.agrogestion.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Operaciones CRUD sobre SQLite (IE 2.3.1):
 *  - Insertar  -> insertar()
 *  - Recuperar -> obtenerTodos()  (Flow: la UI se actualiza sola cuando cambia la tabla)
 *  - Actualizar-> actualizarObservaciones(), marcarTodosSincronizados()
 *  - Eliminar  -> eliminarPorId()
 */
@Dao
interface RegistroDao {
    @Query("SELECT * FROM registros_terreno ORDER BY fechaMillis DESC, id DESC")
    fun obtenerTodos(): Flow<List<RegistroEntity>>

    @Query("SELECT COUNT(*) FROM registros_terreno")
    suspend fun contarRegistros(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(registro: RegistroEntity): Long

    @Query("UPDATE registros_terreno SET observacionesFoliares = :texto, sincronizado = 0 WHERE id = :registroId")
    suspend fun actualizarObservaciones(registroId: Long, texto: String)

    @Query("DELETE FROM registros_terreno WHERE id = :registroId")
    suspend fun eliminarPorId(registroId: Long)

    @Query("UPDATE registros_terreno SET sincronizado = 1 WHERE sincronizado = 0")
    suspend fun marcarTodosSincronizados()
}
