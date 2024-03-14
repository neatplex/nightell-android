package com.neatplex.nightell.repository

import com.neatplex.nightell.dto.PostCollection
import com.neatplex.nightell.dto.PostUpdateRequest
import com.neatplex.nightell.dto.PostUploadRequest
import com.neatplex.nightell.dto.PostStoreResponse
import com.neatplex.nightell.network.ApiService
import com.neatplex.nightell.util.Result
import javax.inject.Inject

class PostRepository @Inject constructor(private val apiService: ApiService) {


    suspend fun showFeed(lastId: Int?) : Result<PostCollection> {
        return try {
            val response = apiService.showFeed(lastId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error("Error fetching posts", null)
        }
    }

    suspend fun uploadPost(title: String, description: String?, audioId: Int, imageId: Int?): Result<PostStoreResponse?> {

        val request = PostUploadRequest(title,description, audioId, imageId)

        return try {
            val response = apiService.uploadPost(request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body())
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun showUserPosts(userId: Int, lastId: Int?) : Result<PostCollection?> {
        return try {
            val response = apiService.showUserPosts(userId,lastId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body())
            } else {
                Result.Error(response.message(), response.code())
            }
        }catch (e: Exception){
            Result.Error(e.message ?: "An error occurred")

        }
    }

    suspend fun editPost(title: String, description: String, postId: Int) : Result<PostStoreResponse?>{
        val request = PostUpdateRequest(title,description)
        return try {
            val response = apiService.updatePost(postId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body())
            } else{
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }

    }

    suspend fun deletePost(postId: Int) : Result<Any?>{
        return try {
            val response = apiService.deletePost(postId)
            if (response.isSuccessful) {
                Result.Success("Post Deleted", response.code())
            } else{
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }
}