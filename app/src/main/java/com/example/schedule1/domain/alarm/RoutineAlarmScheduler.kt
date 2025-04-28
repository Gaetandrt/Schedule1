package com.example.schedule1.domain.alarm

import com.example.schedule1.domain.model.Routine

/**
 * Interface for scheduling and cancelling routine reminder alarms.
 */
interface RoutineAlarmScheduler {
    fun schedule(routine: Routine): Unit
    fun cancel(routineId: Long): Unit
}