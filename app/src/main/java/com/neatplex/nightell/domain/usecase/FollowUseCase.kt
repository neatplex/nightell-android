package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.repository.IFollowRepository
import javax.inject.Inject

class FollowUseCase @Inject constructor(private val followRepository: IFollowRepository) {

    suspend fun follow(userId: Int, friendId: Int) = followRepository.follow(userId, friendId)

    suspend fun unfollow(userId: Int, friendId: Int) = followRepository.unfollow(userId, friendId)

    suspend fun followers(userId: Int) = followRepository.followers(userId)

    suspend fun followings(userId: Int) = followRepository.followings(userId)
}
