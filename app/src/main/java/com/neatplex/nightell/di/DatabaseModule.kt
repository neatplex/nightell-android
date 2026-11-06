package com.neatplex.nightell.di

import android.content.Context
import androidx.room.Room
import com.neatplex.nightell.data.local.db.AppDatabase
import com.neatplex.nightell.data.local.db.SavedPostDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSavedPostDao(database: AppDatabase): SavedPostDao = database.savedPostDao()
}
