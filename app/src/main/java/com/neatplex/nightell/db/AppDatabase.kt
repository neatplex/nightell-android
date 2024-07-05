package com.neatplex.nightell.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neatplex.nightell.domain.model.PostEntity

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): SavedPostDao
}
