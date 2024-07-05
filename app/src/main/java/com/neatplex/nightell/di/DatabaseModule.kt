package com.neatplex.nightell.di

import android.content.Context
import androidx.room.Room
import com.neatplex.nightell.db.AppDatabase
import com.neatplex.nightell.db.SavedPostDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePostDao(database: AppDatabase): SavedPostDao {
        return database.postDao()
    }
}