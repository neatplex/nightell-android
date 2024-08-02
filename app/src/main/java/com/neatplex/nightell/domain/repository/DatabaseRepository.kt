package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.db.SavedPostDao
import com.neatplex.nightell.domain.model.PostEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val postDao: SavedPostDao) : IDatabaseRepository{
    override suspend fun insertPost(post: PostEntity) {
        postDao.insertPost(post)
    }

    override suspend fun deletePost(post: PostEntity) {
        postDao.deletePost(post)
    }

    override suspend fun getPostById(id: Int): PostEntity? {
        return postDao.getPostById(id)
    }

    override fun getAllPosts(): Flow<List<PostEntity>> {
        return postDao.getAllPosts()
    }
}