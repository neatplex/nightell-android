package com.neatplex.nightell.domain.repository


import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.utils.handleApiResponse
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService) : IAuthRepository {

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
        } catch (e: HttpException) {
            Result.Failure("HTTP error: ${e.code()} - ${e.message}", e)
        } catch (e: IOException) {
            Result.Failure("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Result.Failure(e.localizedMessage ?: "An error occurred", e)
        }
    }
}