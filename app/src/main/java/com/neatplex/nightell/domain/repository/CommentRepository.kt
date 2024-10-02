package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.Comments
import com.neatplex.nightell.data.dto.PostCommentRequest
import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.handleApiResponse
import javax.inject.Inject


class CommentRepository @Inject constructor(private val apiService: ApiService) : ICommentRepository {

    override suspend fun deleteComment(commentId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteComment(commentId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }

    override suspend fun getPostComments(postId: Int): Result<Comments> {
        return try {
            val response = apiService.getPostComment(postId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }

    override suspend fun postComment(postId: Int, comment: String): Result<Comment> {
        return try {
            val commentRequest = PostCommentRequest(postId, comment)
            val response = apiService.postComment(commentRequest)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }

    override suspend fun getUserComments(userId: Int): Result<Comments> {
        return try {
            val response = apiService.getUserComment(userId)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "An error occurred")
        }
    }
}