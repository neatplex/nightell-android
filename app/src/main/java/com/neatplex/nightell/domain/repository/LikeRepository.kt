package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.Likes
import com.neatplex.nightell.data.dto.StoreLike
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.data.api.ApiService
import com.neatplex.nightell.utils.handleApiResponse
import javax.inject.Inject

class LikeRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun like(postId : Int) : Result<StoreLike?>{
        return try {
            val response = apiService.like(postId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }

    suspend fun showLikes(postId : Int) : Result<Likes?>{
        return try {
            val response = apiService.getLikes(postId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }

    suspend fun deleteLike(likeId : Int) : Result<Unit>{
        return try {
            val response = apiService.deleteLike(likeId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }
}