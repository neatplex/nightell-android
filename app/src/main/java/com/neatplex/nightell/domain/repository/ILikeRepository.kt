package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.Likes
import com.neatplex.nightell.data.dto.StoreLike
import com.neatplex.nightell.utils.Result


interface ILikeRepository {
    suspend fun like(postId: Int): Result<StoreLike>
    suspend fun showLikes(postId: Int): Result<Likes>
    suspend fun deleteLike(likeId: Int): Result<Unit>
}