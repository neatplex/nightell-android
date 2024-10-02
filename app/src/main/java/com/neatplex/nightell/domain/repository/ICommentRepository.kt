package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.Comments
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.utils.Result

interface ICommentRepository {
    suspend fun getPostComments(postId: Int): Result<Comments>
    suspend fun getUserComments(userId: Int): Result<Comments>
    suspend fun postComment(postId: Int, comment: String): Result<Comment>
    suspend fun deleteComment(commentId: Int): Result<Unit>
}