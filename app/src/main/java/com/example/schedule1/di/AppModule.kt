package com.example.scheduleapp.di

import android.content.Context
import androidx.room.Room
import com.example.scheduleapp.data.local.AppDatabase
import com.example.scheduleapp.data.local.RoutineDao
import com.example.scheduleapp.data.repository.RoutineRepositoryImpl
import com.example.scheduleapp.domain.repository.RoutineRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        // .fallbackToDestructiveMigration() // Add if migration strategy needed
        .build()
    }

    @Provides
    fun provideRoutineDao(appDatabase: AppDatabase): RoutineDao {
        return appDatabase.routineDao()
    }

    @Provides
    @Singleton
    fun provideRoutineRepository(routineDao: RoutineDao): RoutineRepository {
        return RoutineRepositoryImpl(routineDao)
    }
}