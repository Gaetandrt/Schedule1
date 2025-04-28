package com.example.scheduleapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Represents a user-defined routine entity for database storage.
 *
 * @property id Unique identifier for the routine (auto-generated).
 * @property name The name of the routine (mandatory).
 * @property description An optional description for the routine.
 * @property scheduleTime A simple representation of the time for the routine (e.g., "HH:mm").
 *                       More complex scheduling (date, recurrence) will be added later.
 */
@Entity(tableName = "routines") // Define the table name
data class Routine(
    @PrimaryKey(autoGenerate = true) // Make id the auto-generating primary key
    val id: Long = 0, // Default value needed for auto-generation
    val name: String,
    val description: String?,
    val scheduleTime: String // Simple time representation for TP1
    // TODO: Add fields for category, periodicity, priority, location triggers etc. in later TPs
) {
    companion object {
        /*
         * Creates a default routine instance.
         * Useful for initial state or examples.
         * Note: The ID will be ignored/overridden by Room on insertion.
         */
        fun createDefault(): Routine {
            return Routine(
                // id = 0, // Let Room handle the ID generation
                name = "Morning Check-in",
                description = "Review schedule and emails.",
                scheduleTime = "09:00"
            )
        }
    }
}