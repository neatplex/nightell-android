package com.neatplex.nightell.repository

import com.neatplex.nightell.dto.ShowProfileResponse
import com.neatplex.nightell.dto.UserUpdated
import com.neatplex.nightell.dto.Users
import com.neatplex.nightell.util.Result
import com.neatplex.nightell.network.ApiService
import com.neatplex.nightell.util.handleApiResponse

import javax.inject.Inject

class UserProfileRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun profile(): Result<ShowProfileResponse?> {
        return try {
            val response = apiService.showProfile()
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }


    suspend fun changeProfileName(name: String): Result<UserUpdated?> {

        return try {
            val requestBody = mapOf("name" to name)
            val response = apiService.changeProfileName(requestBody)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun changeProfileBio(bio: String): Result<UserUpdated?> {

        return try {
            val requestBody = mapOf("bio" to bio)
            val response = apiService.changeProfileBio(requestBody)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun changeProfileUsername(username: String): Result<UserUpdated?> {

        return try {
            val requestBody = mapOf("username" to username)
            val response = apiService.changeProfileUsername(requestBody)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun followers(userId: Int): Result<Users?> {
        return try {
            val response = apiService.userFollowers(userId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")

        }
    }

    suspend fun followings(userId: Int): Result<Users?> {
        return try {
            val response = apiService.userFollowings(userId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }
}