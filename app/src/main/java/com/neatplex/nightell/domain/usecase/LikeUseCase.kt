package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.model.Like
import com.neatplex.nightell.domain.repository.LikeRepository
import com.neatplex.nightell.domain.util.Result
import javax.inject.Inject

class LikeUseCase @Inject constructor(private val likeRepository: LikeRepository) {

    suspend fun like(postId: Int): Result<Like> = likeRepository.like(postId)

    suspend fun getLikes(postId: Int): Result<List<Like>> = likeRepository.getLikes(postId)

    suspend fun unlike(likeId: Int): Result<Unit> = likeRepository.unlike(likeId)
}
