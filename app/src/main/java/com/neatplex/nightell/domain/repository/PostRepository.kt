package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.PostCollection
import com.neatplex.nightell.data.dto.PostUpdateRequest
import com.neatplex.nightell.data.dto.PostUploadRequest
import com.neatplex.nightell.data.dto.PostDetailResponse
import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.handleApiResponse
import javax.inject.Inject

class PostRepository @Inject constructor(private val apiService: ApiService) : IPostRepository{

    override suspend fun showFeed(lastId: Int?) : Result<PostCollection> {
        return try {
            val response = apiService.showFeed(lastId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.localizedMessage ?: "An error occurred", e)
        }
    }

    override suspend fun showUserPosts(userId: Int, lastId: Int?) : Result<PostCollection> {
        return try {
            val response = apiService.showUserPosts(userId,lastId)
            handleApiResponse(response)
        } catch (e: Exception){
            Result.Failure(e.localizedMessage ?: "An error occurred", e)

        }
    }

    override suspend fun uploadPost(title: String, description: String?, audioId: Int, imageId: Int?): Result<PostDetailResponse> {
        val request = PostUploadRequest(title,description, audioId, imageId)
        return try {
            val response = apiService.uploadPost(request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.localizedMessage ?: "An error occurred", e)
        }
    }

    override suspend fun editPost(title: String, description: String, postId: Int) : Result<PostDetailResponse> {
        val request = PostUpdateRequest(title,description)
        return try {
            val response = apiService.updatePost(postId, request)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.localizedMessage ?: "An error occurred", e)
        }

    }

    override suspend fun deletePost(postId: Int) : Result<Unit> {
        return try {
            val response = apiService.deletePost(postId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.localizedMessage ?: "An error occurred", e)
        }
    }

    override suspend fun getPostById(postId: Int): Result<PostDetailResponse> {
        return try {
            val response = apiService.getPostById(postId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.localizedMessage ?: "An error occurred", e)
        }
    }

    override suspend fun search(query: String, lastId: Int?): Result<PostCollection> {
        return try {
            val response = apiService.searchPost(query, lastId)
            handleApiResponse(response)
        }
        catch (e: Exception){
            Result.Failure(e.localizedMessage ?: "An error occurred", e)
        }
    }
}