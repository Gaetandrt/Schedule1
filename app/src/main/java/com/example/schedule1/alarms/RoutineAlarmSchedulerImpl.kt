package com.example.schedule1.alarms

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.schedule1.domain.alarm.RoutineAlarmScheduler
import com.example.schedule1.domain.model.Routine
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject

class RoutineAlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager // Inject AlarmManager
) : RoutineAlarmScheduler {

    override fun schedule(routine: Routine): Unit {
        // Check for exact alarm permission (simplified check)
        // Note: On Android 14+, user must grant this via Settings -> Alarms & Reminders
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.w("AlarmScheduler", "Cannot schedule exact alarm. Permission not granted for routine ${routine.id}.")
            // TODO: Guide user to settings or use inexact alarms
            return
        }

        val intent = Intent(context, RoutineAlarmReceiver::class.java).apply {
            // Ensure the action is unique if needed, though extras make it somewhat unique
            // action = "com.example.schedule1.ALARM_TRIGGER"
            putExtra(RoutineAlarmReceiver.EXTRA_ROUTINE_ID, routine.id)
            putExtra(RoutineAlarmReceiver.EXTRA_ROUTINE_NAME, routine.name)
            putExtra(RoutineAlarmReceiver.EXTRA_ROUTINE_DESC, routine.description)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            routine.id.toInt(), // Use routine ID as request code for uniqueness
            intent,
            pendingIntentFlags
        )

        val triggerTimeMillis = calculateNextTriggerTime(routine.scheduleTime)

        Log.d("AlarmScheduler", "Scheduling routine ${routine.id} (${routine.name}) for ${android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", triggerTimeMillis)}")

        // Schedule the exact alarm
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, // Use RTC_WAKEUP to wake device
                triggerTimeMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "SecurityException scheduling alarm for routine ${routine.id}. Check SCHEDULE_EXACT_ALARM permission.", e)
        }
    }

    override fun cancel(routineId: Long): Unit {
        // Create an Intent that matches the one used for scheduling (extras are not needed for matching here)
        val intent = Intent(context, RoutineAlarmReceiver::class.java)
        // action = "com.example.schedule1.ALARM_TRIGGER" // Must match if action was set

        // Flags must match how the PendingIntent was retrieved/created for cancellation
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }

        // Recreate the *exact* same PendingIntent used for scheduling to cancel it
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            routineId.toInt(), // Use routine ID as request code
            intent,
            pendingIntentFlags // Use FLAG_NO_CREATE to check existence without creating
        )

        if (pendingIntent != null) {
            Log.d("AlarmScheduler", "Cancelling alarm for routine $routineId")
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel() // Also cancel the PendingIntent itself
        } else {
            Log.d("AlarmScheduler", "No alarm found to cancel for routine $routineId (PendingIntent was null)")
        }
    }

    // Calculates the next trigger time based on HH:mm string
    private fun calculateNextTriggerTime(scheduleTime: String): Long {
        val parts = scheduleTime.split(":")
        if (parts.size != 2) {
            Log.w("AlarmScheduler", "Invalid scheduleTime format: $scheduleTime. Scheduling 1 min from now.")
            return System.currentTimeMillis() + 60000
        }

        try {
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis() // Start with current time
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If the calculated time is in the past, schedule for the next day
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            return calendar.timeInMillis
        } catch (e: NumberFormatException) {
            Log.w("AlarmScheduler", "Invalid number format in scheduleTime: $scheduleTime. Scheduling 1 min from now.", e)
            return System.currentTimeMillis() + 60000
        }
    }
}