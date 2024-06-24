package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.PostCollection
import com.neatplex.nightell.data.dto.PostUpdateRequest
import com.neatplex.nightell.data.dto.PostUploadRequest
import com.neatplex.nightell.data.dto.PostStoreResponse
import com.neatplex.nightell.data.api.ApiService
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.handleApiResponse
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(private val apiService: ApiService) : PostRepository{

    override suspend fun showFeed(lastId: Int?) : Result<PostCollection> {
        return try {
            val response = apiService.showFeed(lastId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error("Error fetching posts", null)
        }
    }

    override suspend fun showUserPosts(userId: Int, lastId: Int?) : Result<PostCollection> {
        return try {
            val response = apiService.showUserPosts(userId,lastId)
            handleApiResponse(response)
        }catch (e: Exception){
            Result.Error("Error fetching posts", null)

        }
    }

    override suspend fun uploadPost(title: String, description: String?, audioId: Int, imageId: Int?): Result<PostStoreResponse> {
        val request = PostUploadRequest(title,description, audioId, imageId)
        return try {
            val response = apiService.uploadPost(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error("Error uploading post", null)
        }
    }

    override suspend fun editPost(title: String, description: String, postId: Int) : Result<PostStoreResponse> {
        val request = PostUpdateRequest(title,description)
        return try {
            val response = apiService.updatePost(postId, request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error("Error editing post", null)
        }

    }

    override suspend fun deletePost(postId: Int) : Result<Unit> {
        return try {
            val response = apiService.deletePost(postId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error("Error deleting post", null)
        }
    }

    override suspend fun search(query: String, lastId: Int?): Result<PostCollection> {
        return try {
            val response = apiService.search(query, lastId)
            handleApiResponse(response)
        }
        catch (e: Exception){
            Result.Error(e.message ?: "An error occurred")
        }
    }
}