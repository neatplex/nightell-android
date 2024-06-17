package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.PostCollection
import com.neatplex.nightell.data.dto.PostStoreResponse
import com.neatplex.nightell.utils.Result

interface PostRepository {
    suspend fun showFeed(lastId: Int?) : Result<PostCollection>

    suspend fun showUserPosts(userId: Int, lastId: Int?) : Result<PostCollection>

    suspend fun search(query: String, lastId: Int?): Result<PostCollection>

    suspend fun uploadPost(title: String, description: String?, audioId: Int, imageId: Int?): Result<PostStoreResponse>

    suspend fun editPost(title: String, description: String, postId: Int) : Result<PostStoreResponse>

    suspend fun deletePost(postId: Int) : Result<Unit>
}