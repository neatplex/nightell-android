package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.CommentDetailResponse
import com.neatplex.nightell.data.dto.Comments
import com.neatplex.nightell.utils.Result

interface ICommentRepository {
    suspend fun getPostComments(postId: Int, lastId: Int?): Result<Comments>
    suspend fun getUserComments(userId: Int, lastId: Int?): Result<Comments>
    suspend fun postComment(postId: Int, comment: String): Result<CommentDetailResponse>
    suspend fun deleteComment(commentId: Int): Result<Unit>
}