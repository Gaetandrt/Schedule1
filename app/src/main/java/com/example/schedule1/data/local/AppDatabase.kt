package com.example.scheduleapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.scheduleapp.domain.model.Routine

/**
 * The Room database for this application.
 */
@Database(
    entities = [Routine::class], // List all entities here
    version = 1,                 // Increment version on schema changes
    exportSchema = false         // Recommended to disable for non-library apps
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to the RoutineDao.
     */
    abstract fun routineDao(): RoutineDao

    // Companion object can be used for singleton instance or pre-population
    // but Dependency Injection (e.g., Hilt) is generally preferred for providing the instance.
    companion object {
        const val DATABASE_NAME = "schedule_app.db"
    }
}