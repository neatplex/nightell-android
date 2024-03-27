package com.neatplex.nightell.repository


import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.data.api.ApiService
import com.neatplex.nightell.utils.handleApiResponse
import javax.inject.Inject

class UserAuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun register(username: String, email: String, password: String): Result<AuthResponse?> {
        return try {
            val request = RegistrationRequest(username, email, password)
            val response = apiService.register(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun loginWithEmail(email: String, password: String): Result<AuthResponse?> {
        return try {
            val request = LoginEmailRequest(email, password)
            val response = apiService.loginWithEmail(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun loginWithUsername(username: String, password: String): Result<AuthResponse?> {
        return try {
            val request = LoginUsernameRequest(username, password)
            val response = apiService.loginWithUsername(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }
}