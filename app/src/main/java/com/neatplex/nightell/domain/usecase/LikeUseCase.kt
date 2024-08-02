package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.repository.ILikeRepository
import javax.inject.Inject

class LikeUseCase @Inject constructor(private val likeRepository: ILikeRepository) {

    suspend fun like(postId : Int) = likeRepository.like(postId)

    suspend fun showLikes(postId : Int) = likeRepository.showLikes(postId)

    suspend fun deleteLike(likeId : Int) = likeRepository.deleteLike(likeId)
}