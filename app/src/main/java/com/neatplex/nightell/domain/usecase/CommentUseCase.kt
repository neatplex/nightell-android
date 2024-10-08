package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.CommentDetailResponse
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.domain.repository.ICommentRepository
import javax.inject.Inject
import com.neatplex.nightell.utils.Result


class CommentUseCase @Inject constructor(private val commentRepository: ICommentRepository) {

    suspend fun deleteComment(commentId: Int) = commentRepository.deleteComment(commentId)

    suspend fun getPostComments(postId: Int, lastCommentId: Int?) : Result<List<Comment>> {
        val result = commentRepository.getPostComments(postId, lastCommentId)
        return if (result is Result.Success) {
            val newComments = result.data?.comments ?: emptyList()
            Result.Success(newComments)
        } else {
            Result.Failure("Error loading comments", null)
        }
    }

    suspend fun postComment(postId: Int, comment: String) : Result<CommentDetailResponse> {
        return commentRepository.postComment(postId, comment)
    }

    suspend fun getUserComments(userId: Int, lastCommentId: Int?) : Result<List<Comment>> {
        val result = commentRepository.getUserComments(userId, lastCommentId)
        return if (result is Result.Success) {
            val newComments = result.data?.comments ?: emptyList()
            Result.Success(newComments)
        } else {
            Result.Failure("Error loading user comments", null)
        }
    }
}