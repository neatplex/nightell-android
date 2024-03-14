package com.neatplex.nightell.repository

import com.neatplex.nightell.dto.Likes
import com.neatplex.nightell.dto.StoreLike
import com.neatplex.nightell.util.Result
import com.neatplex.nightell.network.ApiService
import javax.inject.Inject

class LikeRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun like(postId : Int) : Result<StoreLike?>{
        return try {
            val response = apiService.like(postId)

            if (response.isSuccessful) {
                Result.Success(response.body())
            } else{
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun showLikes(postId : Int) : Result<Likes?>{
        return try {
            val response = apiService.getLikes(postId)

            if (response.isSuccessful) {
                Result.Success(response.body())
            } else{
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun deleteLike(likeId : Int) : Result<Any?>{
        return try {
            val response = apiService.deleteLike(likeId)

            if (response.isSuccessful) {
                Result.Success(response.body())
            } else{
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }
}