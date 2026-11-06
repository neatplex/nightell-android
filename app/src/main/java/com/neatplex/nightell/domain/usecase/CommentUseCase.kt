package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.domain.repository.CommentRepository
import com.neatplex.nightell.domain.util.Page
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.map
import javax.inject.Inject

class CommentUseCase @Inject constructor(private val commentRepository: CommentRepository) {

    suspend fun getPostComments(postId: Int, lastCommentId: Int?): Result<Page<Comment>> =
        commentRepository.getPostComments(postId, lastCommentId).map { Page.of(it) }

    suspend fun getUserComments(userId: Int, lastCommentId: Int?): Result<Page<Comment>> =
        commentRepository.getUserComments(userId, lastCommentId).map { Page.of(it) }

    suspend fun postComment(postId: Int, text: String): Result<Comment> =
        commentRepository.addComment(postId, text)

    suspend fun deleteComment(commentId: Int): Result<Unit> =
        commentRepository.deleteComment(commentId)
}
