package com.example.scheduleapp.domain.repository

import com.example.scheduleapp.domain.model.Routine
import kotlinx.coroutines.flow.Flow

/**
 * Interface for accessing Routine data.
 * This abstracts the data source (local or remote) from the rest of the application.
 */
interface RoutineRepository {

    fun observeAllRoutines(): Flow<List<Routine>>

    fun observeRoutineById(routineId: Long): Flow<Routine?>

    suspend fun addRoutine(routine: Routine): Long

    suspend fun updateRoutine(routine: Routine)

    suspend fun upsertRoutine(routine: Routine)

    suspend fun deleteRoutine(routine: Routine)

    suspend fun deleteRoutineById(routineId: Long)
}