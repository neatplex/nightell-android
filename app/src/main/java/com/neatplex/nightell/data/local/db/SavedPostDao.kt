package com.neatplex.nightell.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: SavedPostEntity)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM posts WHERE id = :id)")
    suspend fun exists(id: Int): Boolean

    @Query("SELECT * FROM posts")
    fun observeAll(): Flow<List<SavedPostEntity>>
}
