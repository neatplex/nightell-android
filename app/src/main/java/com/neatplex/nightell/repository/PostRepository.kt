package com.neatplex.nightell.repository

import com.neatplex.nightell.dto.PostCollection
import com.neatplex.nightell.dto.PostUpdateRequest
import com.neatplex.nightell.dto.PostUploadRequest
import com.neatplex.nightell.dto.PostStoreResponse
import com.neatplex.nightell.network.ApiService
import com.neatplex.nightell.util.Result
import com.neatplex.nightell.util.handleApiResponse
import javax.inject.Inject

class PostRepository @Inject constructor(private val apiService: ApiService) {


    suspend fun showFeed(lastId: Int?) : Result<PostCollection?> {
        return try {
            val response = apiService.showFeed(lastId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error("Error fetching posts", null)
        }
    }

    suspend fun uploadPost(title: String, description: String?, audioId: Int, imageId: Int?): Result<PostStoreResponse?> {

        val request = PostUploadRequest(title,description, audioId, imageId)

        return try {
            val response = apiService.uploadPost(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error("Error uploading post", null)
        }
    }

    suspend fun showUserPosts(userId: Int, lastId: Int?) : Result<PostCollection?> {
        return try {
            val response = apiService.showUserPosts(userId,lastId)
            handleApiResponse(response)
        }catch (e: Exception){
            Result.Error("Error fetching posts", null)

        }
    }

    suspend fun editPost(title: String, description: String, postId: Int) : Result<PostStoreResponse?>{
        val request = PostUpdateRequest(title,description)
        return try {
            val response = apiService.updatePost(postId, request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error("Error editing post", null)
        }

    }

    suspend fun deletePost(postId: Int) : Result<Any?>{
        return try {
            val response = apiService.deletePost(postId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error("Error deleting post", null)
        }
    }
}