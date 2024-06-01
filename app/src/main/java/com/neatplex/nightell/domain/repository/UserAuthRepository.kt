package com.neatplex.nightell.domain.repository


import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.data.api.ApiService
import com.neatplex.nightell.utils.Validation
import com.neatplex.nightell.utils.handleApiResponse
import javax.inject.Inject

class UserAuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun register(request: RegistrationRequest): Result<AuthResponse?> {
        return try {
            val response = apiService.register(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun loginWithEmail(request: LoginEmailRequest): Result<AuthResponse?> {
        return try {
            val response = apiService.loginWithEmail(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun loginWithUsername(request: LoginUsernameRequest): Result<AuthResponse?> {
        return try {
            val response = apiService.loginWithUsername(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }
}