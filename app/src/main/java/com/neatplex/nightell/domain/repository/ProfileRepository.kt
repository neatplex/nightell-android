package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.handleApiResponse
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apiService: ApiService) : IProfileRepository {

    override suspend fun fetchProfile(): Result<Profile> {
        return try {
            val response = apiService.showProfile()
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }

    override suspend fun showUserProfile(userId: Int): Result<Profile> {
        return try {
            val response = apiService.showUserProfile(userId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")

        }
    }

    override suspend fun changeProfileName(name: String): Result<UserUpdated> {

        return try {
            val requestBody = mapOf("name" to name)
            val response = apiService.changeProfileName(requestBody)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }

    override suspend fun changeProfileBio(bio: String): Result<UserUpdated> {

        return try {
            val requestBody = mapOf("bio" to bio)
            val response = apiService.changeProfileBio(requestBody)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }

    override suspend fun changeProfileUsername(username: String): Result<UserUpdated> {
        return try {
            val requestBody = mapOf("username" to username)
            val response = apiService.changeProfileUsername(requestBody)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val response = apiService.deleteAccount()
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }
}