package com.neatplex.nightell.utils

import org.json.JSONObject
import retrofit2.Response

// Result Sealed Class
sealed class Result<out T> {
    data class Success<out T>(val data: T?, val code: Int? = null) : Result<T>()
    data class Error(val message: String, val exception: Throwable? = null, val code: Int? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// API Response Handler
fun <T> handleApiResponse(response: Response<T>): Result<T?> {
    return if (response.isSuccessful) {
        Result.Success(response.body(), response.code())
    } else {
        val errorMessage = when (response.code()) {
            in 400..499 -> {
                val errorBody = response.errorBody()?.string()
                try {
                    JSONObject(errorBody).getString("message")
                } catch (e: Exception) {
                    "Error occurred, please try again"
                }
            }
            in 500..599 -> "Internal server error!"
            else -> "Something went wrong, please try again later!"
        }
        Result.Error(errorMessage, null, response.code())
    }
}