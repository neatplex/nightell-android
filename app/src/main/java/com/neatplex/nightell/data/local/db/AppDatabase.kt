package com.neatplex.nightell.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SavedPostEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedPostDao(): SavedPostDao

    companion object {
        const val NAME = "app_database"
    }
}
