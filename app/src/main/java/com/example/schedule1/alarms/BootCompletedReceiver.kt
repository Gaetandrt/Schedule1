package com.example.schedule1.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.schedule1.domain.alarm.RoutineAlarmScheduler
import com.example.schedule1.domain.repository.RoutineRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: RoutineRepository
    @Inject lateinit var scheduler: RoutineAlarmScheduler
    @Inject lateinit var applicationScope: CoroutineScope // Use the same scope from AppModule

    override fun onReceive(context: Context, intent: Intent): Unit {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootCompletedReceiver", "Device booted, rescheduling alarms...")
            applicationScope.launch(Dispatchers.IO) {
                val routines = repository.observeAllRoutines().firstOrNull() // Get current routines
                if (routines == null) {
                    Log.d("BootCompletedReceiver", "No routines found to reschedule.")
                    return@launch
                }
                routines.forEach { routine ->
                    Log.d("BootCompletedReceiver", "Rescheduling routine ${routine.id}")
                    scheduler.schedule(routine)
                }
                 Log.d("BootCompletedReceiver", "Alarm rescheduling complete.")
            }
        }
    }
}