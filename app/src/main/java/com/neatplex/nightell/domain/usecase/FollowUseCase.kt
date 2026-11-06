package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.FollowRepository
import com.neatplex.nightell.domain.util.Page
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.map
import javax.inject.Inject

class FollowUseCase @Inject constructor(private val followRepository: FollowRepository) {

    suspend fun follow(userId: Int): Result<Unit> = followRepository.follow(userId)

    suspend fun unfollow(userId: Int): Result<Unit> = followRepository.unfollow(userId)

    suspend fun followers(userId: Int, lastId: Int?, count: Int): Result<Page<User>> =
        followRepository.getFollowers(userId, lastId, count).map { Page.of(it, pageSize = count) }

    suspend fun followings(userId: Int, lastId: Int?, count: Int): Result<Page<User>> =
        followRepository.getFollowings(userId, lastId, count).map { Page.of(it, pageSize = count) }
}
