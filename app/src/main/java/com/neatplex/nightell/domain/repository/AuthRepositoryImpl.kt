package com.neatplex.nightell.domain.repository


import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.data.api.ApiService
import com.neatplex.nightell.utils.handleApiResponse
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val apiService: ApiService) : AuthRepository {

    override suspend fun register(request: RegistrationRequest): Result<AuthResponse> {
        return try {
            val response = apiService.register(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.localizedMessage ?: "An error occurred", e)
        }
    }

    override suspend fun loginWithEmail(request: LoginEmailRequest): Result<AuthResponse> {
        return try {
            val response = apiService.loginWithEmail(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.localizedMessage ?: "An error occurred", e)
        }
    }

    override suspend fun loginWithUsername(request: LoginUsernameRequest): Result<AuthResponse> {
        return try {
            val response = apiService.loginWithUsername(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.localizedMessage ?: "An error occurred", e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthResponse> {
        // Make API call to your backend to authenticate with Google ID token
        return try {
            val requestBody = mapOf("google_token" to idToken)
            val response = apiService.signInWithGoogle(requestBody)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.localizedMessage ?: "An error occurred", e)
        }
    }
}