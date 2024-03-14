package com.neatplex.nightell.repository


import com.neatplex.nightell.util.Result
import com.neatplex.nightell.dto.AuthResponse
import com.neatplex.nightell.dto.LoginEmailRequest
import com.neatplex.nightell.dto.LoginUsernameRequest
import com.neatplex.nightell.dto.RegistrationRequest
import com.neatplex.nightell.network.ApiService
import org.json.JSONObject
import javax.inject.Inject

class UserAuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun register(username: String, email: String, password: String): Result<AuthResponse?> {
        return try {
            val request = RegistrationRequest(username, email, password)
            val response = apiService.register(request)

            if (response.isSuccessful) {
                Result.Success(response.body())
            } else {
                if(response.code() in 401..499){
                    val errorBody = response.errorBody()?.string()
                    val message = JSONObject(errorBody).getString("message")
                    Result.Error(message, response.code())
                }else{
                    Result.Error("Internal server error. Try later!", response.code())
                }
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun loginWithEmail(email: String, password: String): Result<AuthResponse?> {
        return try {
            val request = LoginEmailRequest(email, password)
            val response = apiService.loginWithEmail(request)

            if (response.isSuccessful) {
                Result.Success(response.body())
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun loginWithUsername(username: String, password: String): Result<AuthResponse?> {
        return try {
            val request = LoginUsernameRequest(username, password)
            val response = apiService.loginWithUsername(request)

            if (response.isSuccessful) {
                Result.Success(response.body())
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }
}