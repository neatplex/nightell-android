package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.utils.handleApiResponse

import javax.inject.Inject

class FollowRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun follow(userId: Int, friendId: Int): Result<Unit> {
        return try {
            val response = apiService.follow(userId, friendId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")

        }
    }

    suspend fun unfollow(userId: Int, friendId: Int): Result<Unit> {
        return try {
            val response = apiService.unfollow(userId, friendId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")

        }
    }

    suspend fun followers(userId: Int): Result<Users> {
        return try {
            val response = apiService.userFollowers(userId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")

        }
    }

    suspend fun followings(userId: Int): Result<Users> {
        return try {
            val response = apiService.userFollowings(userId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }
}