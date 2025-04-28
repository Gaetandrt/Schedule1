package com.example.scheduleapp.data.repository

import com.example.scheduleapp.data.local.RoutineDao
import com.example.scheduleapp.domain.model.Routine
import com.example.scheduleapp.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject // Assuming Hilt or another DI framework
import javax.inject.Singleton

/**
 * Implementation of the RoutineRepository that uses a local Room database.
 */
@Singleton // Or appropriate scope if using DI
class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao
) : RoutineRepository {

    override fun observeAllRoutines(): Flow<List<Routine>> {
        return routineDao.observeAllRoutines()
    }

    override fun observeRoutineById(routineId: Long): Flow<Routine?> {
        return routineDao.observeRoutineById(routineId)
    }

    override suspend fun addRoutine(routine: Routine): Long {
        // Ensure ID is 0 for insertion if it's an auto-generate key
        // Or rely on the upsert/insert logic if appropriate
        return routineDao.insertRoutine(routine.copy(id = 0))
    }

    override suspend fun updateRoutine(routine: Routine) {
        routineDao.updateRoutine(routine)
    }

    override suspend fun upsertRoutine(routine: Routine) {
        routineDao.upsertRoutine(routine)
    }

    override suspend fun deleteRoutine(routine: Routine) {
        routineDao.deleteRoutine(routine)
    }

    override suspend fun deleteRoutineById(routineId: Long) {
        routineDao.deleteRoutineById(routineId)
    }
}