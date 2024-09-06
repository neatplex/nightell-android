package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.UserResponse
import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.handleApiResponse
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService) : IUserRepository {

    override suspend fun fetchProfile(): Result<Profile> {
        return try {
            val response = apiService.showProfile()
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }

    override suspend fun showUserProfile(userId: Int): Result<UserResponse> {
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

    override suspend fun changeProfileImage(id: Int): Result<UserUpdated> {
        return try {
            val requestBody = mapOf("image_id" to id)
            val response = apiService.changeProfileImage(requestBody)
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

    override suspend fun searchUser(query: String, lastId: Int?): Result<Users> {
        return try {
            val response = apiService.searchUser(query, lastId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }
}