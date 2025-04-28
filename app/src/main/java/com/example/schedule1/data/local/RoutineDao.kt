package com.example.schedule1.data.local

import androidx.room.*
import com.example.schedule1.domain.model.Routine
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Routine entity.
 */
@Dao
interface RoutineDao {

    /**
     * Observes the list of all routines.
     * @return A flow emitting the list of routines whenever the data changes.
     */
    @Query("SELECT * FROM routines ORDER BY name ASC")
    fun observeAllRoutines(): Flow<List<Routine>>

    /**
     * Observes a single routine by its ID.
     * @param routineId The ID of the routine to observe.
     * @return A flow emitting the routine when found, or null if not found.
     */
    @Query("SELECT * FROM routines WHERE id = :routineId")
    fun observeRoutineById(routineId: Long): Flow<Routine?>

    /**
     * Inserts a routine into the table. If the routine already exists, it replaces it.
     * Use {@link insertRoutine} or {@link updateRoutine} for specific operations.
     *
     * @param routine The routine to be inserted or updated.
     */
    @Upsert
    suspend fun upsertRoutine(routine: Routine)

    /**
     * Inserts a new routine.
     * @param routine The routine to insert.
     * @return The row ID of the newly inserted routine.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRoutine(routine: Routine): Long

    /**
     * Updates an existing routine.
     * @param routine The routine to update.
     * @return The number of rows affected (should be 1).
     */
    @Update
    suspend fun updateRoutine(routine: Routine): Int

    /**
     * Deletes a routine from the table.
     * @param routine The routine to delete.
     * @return The number of rows affected (should be 1).
     */
    @Delete
    suspend fun deleteRoutine(routine: Routine): Int

    /**
     * Deletes a routine by its ID.
     * @param routineId The ID of the routine to delete.
     * @return The number of rows affected.
     */
    @Query("DELETE FROM routines WHERE id = :routineId")
    suspend fun deleteRoutineById(routineId: Long): Int
}