package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.domain.model.PostEntity
import kotlinx.coroutines.flow.Flow

interface IDatabaseRepository {
    suspend fun insertPost(post: PostEntity)
    suspend fun deletePost(post: PostEntity)
    suspend fun getPostById(id: Int): PostEntity?
    fun getAllPosts(): Flow<List<PostEntity>>
}