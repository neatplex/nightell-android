package com.neatplex.nightell.repository

import com.neatplex.nightell.dto.ShowProfileResponse
import com.neatplex.nightell.network.ApiService
import com.neatplex.nightell.util.Result
import com.neatplex.nightell.util.handleApiResponse
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun showUserProfile(userId: Int) : Result<ShowProfileResponse?> {
        return try {
            val response = apiService.showUserProfile(userId)
            handleApiResponse(response)
        }catch (e: Exception){
            Result.Error(e.message ?: "An error occurred")

        }
    }

    suspend fun follow(userId: Int, friendId: Int): Result<Any?> {
        return try {
            val response = apiService.follow(userId, friendId)
            handleApiResponse(response)
        }catch (e: Exception){
            Result.Error(e.message ?: "An error occurred")

        }
    }

    suspend fun unfollow(userId: Int, friendId: Int): Result<Any?> {
        return try {
            val response = apiService.unfollow(userId, friendId)
            handleApiResponse(response)
        }catch (e: Exception){
            Result.Error(e.message ?: "An error occurred")

        }
    }
}