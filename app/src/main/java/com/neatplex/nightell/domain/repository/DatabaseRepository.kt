package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.db.SavedPostDao
import com.neatplex.nightell.domain.model.PostEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val postDao: SavedPostDao) {
    suspend fun insertPost(post: PostEntity) {
        postDao.insertPost(post)
    }

    suspend fun deletePost(post: PostEntity) {
        postDao.deletePost(post)
    }

    suspend fun getPostById(id: Int): PostEntity? {
        return postDao.getPostById(id)
    }

    fun getAllPosts(): Flow<List<PostEntity>> {
        return postDao.getAllPosts()
    }
}