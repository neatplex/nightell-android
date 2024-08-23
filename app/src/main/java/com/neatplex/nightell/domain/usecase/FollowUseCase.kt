package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.IFollowRepository
import javax.inject.Inject
import com.neatplex.nightell.utils.Result


class FollowUseCase @Inject constructor(private val followRepository: IFollowRepository) {

    suspend fun follow(userId: Int) = followRepository.follow(userId)

    suspend fun unfollow(userId: Int) = followRepository.unfollow(userId)

    suspend fun followers(userId: Int, lastId: Int?, count: Int?) : Result<List<User>> {
        val result = followRepository.followers(userId, lastId, count)
        return if (result is Result.Success) {
            val newFollowers = result.data?.users ?: emptyList()
            Result.Success(newFollowers)
        } else {
            Result.Failure("Error in loading followers", null)
        }
    }

    suspend fun followings(userId: Int, lastId: Int?, count: Int?) : Result<List<User>> {
        val result = followRepository.followings(userId, lastId, count)
        return if (result is Result.Success) {
            val newFollowings = result.data?.users ?: emptyList()
            Result.Success(newFollowings)
        } else {
            Result.Failure("Error in loading followings", null)
        }
    }
}
