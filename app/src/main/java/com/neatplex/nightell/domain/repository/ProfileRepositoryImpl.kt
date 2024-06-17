package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.ShowProfileResponse
import com.neatplex.nightell.data.api.ApiService
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.handleApiResponse
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(private val apiService: ApiService) : ProfileRepository {

    override suspend fun profile(): Result<ShowProfileResponse> {
        return try {
            val response = apiService.showProfile()
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun showUserProfile(userId: Int): Result<ShowProfileResponse> {
        return try {
            val response = apiService.showUserProfile(userId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")

        }
    }

    override suspend fun changeProfileName(name: String): Result<UserUpdated> {

        return try {
            val requestBody = mapOf("name" to name)
            val response = apiService.changeProfileName(requestBody)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun changeProfileBio(bio: String): Result<UserUpdated> {

        return try {
            val requestBody = mapOf("bio" to bio)
            val response = apiService.changeProfileBio(requestBody)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun changeProfileUsername(username: String): Result<UserUpdated> {

        return try {
            val requestBody = mapOf("username" to username)
            val response = apiService.changeProfileUsername(requestBody)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

}