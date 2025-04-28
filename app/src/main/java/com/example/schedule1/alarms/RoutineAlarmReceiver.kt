package com.example.schedule1.alarms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.schedule1.R
import com.example.schedule1.presentation.main.MainActivity

class RoutineAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ROUTINE_ID = "com.example.schedule1.EXTRA_ROUTINE_ID"
        const val EXTRA_ROUTINE_NAME = "com.example.schedule1.EXTRA_ROUTINE_NAME"
        const val EXTRA_ROUTINE_DESC = "com.example.schedule1.EXTRA_ROUTINE_DESC"
        const val CHANNEL_ID = "routine_alarms_channel"
        const val CHANNEL_NAME = "Routine Reminders"
    }

    override fun onReceive(context: Context, intent: Intent): Unit {
        val routineId: Long = intent.getLongExtra(EXTRA_ROUTINE_ID, -1L)
        val routineName: String? = intent.getStringExtra(EXTRA_ROUTINE_NAME)
        val routineDesc: String? = intent.getStringExtra(EXTRA_ROUTINE_DESC)

        if (routineId == -1L || routineName == null) {
            // Invalid data, cannot show notification
            return
        }

        createNotificationChannel(context)

        // Intent to launch MainActivity when notification is tapped
        val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Optionally add extras to navigate to a specific routine detail later
        }
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val contentPendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            routineId.hashCode(), // Use routineId based request code for uniqueness
            mainActivityIntent,
            pendingIntentFlags
        )

        // Build the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with a proper notification icon
            .setContentTitle(routineName)
            .setContentText(routineDesc ?: "Time for your routine!")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for reminders
            .setContentIntent(contentPendingIntent) // Set tap action
            .setAutoCancel(true) // Dismiss notification on tap
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationManager = NotificationManagerCompat.from(context)

        // Basic permission check before notifying (won't work if permission denied *after* scheduling)
        // Ideally, scheduling should fail if permission isn't granted initially.
        try {
            // Use routineId as the notification ID
            notificationManager.notify(routineId.toInt(), builder.build())
        } catch (e: SecurityException) {
            // Handle case where POST_NOTIFICATIONS permission is missing
            // Log.e("RoutineAlarmReceiver", "Missing POST_NOTIFICATIONS permission", e)
        }
    }

    private fun createNotificationChannel(context: Context): Unit {
        // Create the NotificationChannel, but only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH // High importance for reminders
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Channel for routine reminder notifications"
                // Configure other channel settings if needed (vibration, lights, etc.)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}