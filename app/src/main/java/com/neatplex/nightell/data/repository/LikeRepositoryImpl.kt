package com.neatplex.nightell.data.repository

import com.neatplex.nightell.data.mapper.toDomain
import com.neatplex.nightell.data.remote.api.ApiService
import com.neatplex.nightell.data.remote.apiCall
import com.neatplex.nightell.data.remote.apiCallNoContent
import com.neatplex.nightell.domain.model.Like
import com.neatplex.nightell.domain.repository.LikeRepository
import com.neatplex.nightell.domain.util.Result
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(private val api: ApiService) : LikeRepository {

    override suspend fun like(postId: Int): Result<Like> =
        apiCall({ api.like(postId) }) { it.like.toDomain() }

    override suspend fun getLikes(postId: Int): Result<List<Like>> =
        apiCall({ api.getLikes(postId) }) { response -> response.likes.orEmpty().map { it.toDomain() } }

    override suspend fun unlike(likeId: Int): Result<Unit> =
        apiCallNoContent { api.deleteLike(likeId) }
}
