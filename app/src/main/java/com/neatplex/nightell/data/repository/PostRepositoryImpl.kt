package com.neatplex.nightell.data.repository

import com.neatplex.nightell.data.mapper.toDomain
import com.neatplex.nightell.data.remote.api.ApiService
import com.neatplex.nightell.data.remote.apiCall
import com.neatplex.nightell.data.remote.apiCallNoContent
import com.neatplex.nightell.data.remote.dto.PostListResponse
import com.neatplex.nightell.data.remote.dto.PostUpdateRequest
import com.neatplex.nightell.data.remote.dto.PostUploadRequest
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.repository.PostRepository
import com.neatplex.nightell.domain.util.Result
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(private val api: ApiService) : PostRepository {

    override suspend fun getFeed(lastId: Int?): Result<List<Post>> =
        apiCall({ api.showFeed(lastId) }, ::toPosts)

    override suspend fun getUserPosts(userId: Int, lastId: Int?): Result<List<Post>> =
        apiCall({ api.showUserPosts(userId, lastId) }, ::toPosts)

    override suspend fun search(query: String, lastId: Int?): Result<List<Post>> =
        apiCall({ api.searchPost(query, lastId) }, ::toPosts)

    override suspend fun getPost(postId: Int): Result<Post> =
        apiCall({ api.getPostById(postId) }) { it.post.toDomain() }

    override suspend fun createPost(
        title: String,
        description: String?,
        audioId: Int,
        imageId: Int?,
    ): Result<Post> =
        apiCall({ api.uploadPost(PostUploadRequest(title, description, audioId, imageId)) }) {
            it.post.toDomain()
        }

    override suspend fun updatePost(postId: Int, title: String, description: String?): Result<Post> =
        apiCall({ api.updatePost(postId, PostUpdateRequest(title, description)) }) { it.post.toDomain() }

    override suspend fun deletePost(postId: Int): Result<Unit> =
        apiCallNoContent { api.deletePost(postId) }

    private fun toPosts(response: PostListResponse) = response.posts.orEmpty().map { it.toDomain() }
}
