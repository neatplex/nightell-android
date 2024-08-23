package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.utils.handleApiResponse

import javax.inject.Inject

class FollowRepository @Inject constructor(private val apiService: ApiService) : IFollowRepository {

    override suspend fun follow(userId: Int): Result<Unit> {
        return try {
            val response = apiService.follow(userId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")

        }
    }

    override suspend fun unfollow(userId: Int): Result<Unit> {
        return try {
            val response = apiService.unfollow(userId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")

        }
    }

    override suspend fun followers(userId: Int, lastId: Int?, count: Int?): Result<Users> {
        return try {
            val response = apiService.userFollowers(userId,lastId,count)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")

        }
    }

    override suspend fun followings(userId: Int, lastId: Int?, count: Int?): Result<Users> {
        return try {
            val response = apiService.userFollowings(userId, lastId, count)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }
}