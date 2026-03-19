package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.RoundRobinHistoryDao
import com.example.myapplication.data.repository.RoundRobinHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "badminton_db"
        ).build()
    }

    @Provides
    fun provideRoundRobinHistoryDao(database: AppDatabase): RoundRobinHistoryDao {
        return database.roundRobinHistoryDao()
    }

    @Provides
    @Singleton
    fun provideRoundRobinHistoryRepository(dao: RoundRobinHistoryDao): RoundRobinHistoryRepository {
        return RoundRobinHistoryRepository(dao)
    }
}
