package com.example.schedule1.di

import android.app.AlarmManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.schedule1.data.local.AppDatabase
import com.example.schedule1.data.local.RoutineDao
import com.example.schedule1.data.repository.RoutineRepositoryImpl
import com.example.schedule1.domain.model.Routine
import com.example.schedule1.domain.repository.RoutineRepository
import com.example.schedule1.alarms.RoutineAlarmSchedulerImpl
import com.example.schedule1.domain.alarm.RoutineAlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provide a CoroutineScope for database operations during creation
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext appContext: Context,
        // Use Provider for lazy DAO injection into callback
        routineDaoProvider: Provider<RoutineDao>,
        applicationScope: CoroutineScope
    ): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase): Unit {
                    super.onCreate(db)
                    // Pre-populate on creation
                    applicationScope.launch(Dispatchers.IO) {
                        val dao: RoutineDao = routineDaoProvider.get()
                        dao.insertRoutine(Routine.createDefault()) // Insert default routine
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideRoutineDao(appDatabase: AppDatabase): RoutineDao {
        return appDatabase.routineDao()
    }

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return ContextCompat.getSystemService(context, AlarmManager::class.java) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideRoutineAlarmScheduler(
        @ApplicationContext context: Context,
        alarmManager: AlarmManager
    ): RoutineAlarmScheduler {
        return RoutineAlarmSchedulerImpl(context, alarmManager)
    }

    @Provides
    @Singleton
    fun provideRoutineRepository(routineDao: RoutineDao): RoutineRepository {
        // Assuming RoutineRepositoryImpl exists and is correct
        return RoutineRepositoryImpl(routineDao)
    }
}