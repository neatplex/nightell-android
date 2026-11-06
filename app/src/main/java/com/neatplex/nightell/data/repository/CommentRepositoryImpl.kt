package com.neatplex.nightell.data.repository

import com.neatplex.nightell.data.mapper.toDomain
import com.neatplex.nightell.data.remote.api.ApiService
import com.neatplex.nightell.data.remote.apiCall
import com.neatplex.nightell.data.remote.apiCallNoContent
import com.neatplex.nightell.data.remote.dto.CommentListResponse
import com.neatplex.nightell.data.remote.dto.PostCommentRequest
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.domain.repository.CommentRepository
import com.neatplex.nightell.domain.util.Result
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(private val api: ApiService) : CommentRepository {

    override suspend fun getPostComments(postId: Int, lastId: Int?): Result<List<Comment>> =
        apiCall({ api.getPostComment(postId, lastId) }, ::toComments)

    override suspend fun getUserComments(userId: Int, lastId: Int?): Result<List<Comment>> =
        apiCall({ api.getUserComment(userId, lastId) }, ::toComments)

    override suspend fun addComment(postId: Int, text: String): Result<Comment> =
        apiCall({ api.postComment(PostCommentRequest(postId, text)) }) { it.comment.toDomain() }

    override suspend fun deleteComment(commentId: Int): Result<Unit> =
        apiCallNoContent { api.deleteComment(commentId) }

    private fun toComments(response: CommentListResponse) =
        response.comments.orEmpty().map { it.toDomain() }
}
