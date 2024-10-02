package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.repository.ICommentRepository
import javax.inject.Inject

class CommentUseCase @Inject constructor(private val commentRepository: ICommentRepository) {
    suspend fun deleteComment(commentId: Int) = commentRepository.deleteComment(commentId)
    suspend fun getPostComments(postId: Int) = commentRepository.getPostComments(postId)
    suspend fun postComment(postId: Int, comment: String) = commentRepository.postComment(postId, comment)
    suspend fun getUserComments(userId: Int) = commentRepository.getUserComments(userId)
}