package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.repository.PostRepository
import com.neatplex.nightell.domain.util.Page
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.map
import javax.inject.Inject

class PostUseCase @Inject constructor(private val postRepository: PostRepository) {

    suspend fun loadFeed(lastPostId: Int?): Result<Page<Post>> =
        postRepository.getFeed(lastPostId).map { Page.of(it) }

    suspend fun loadUserPosts(userId: Int, lastPostId: Int?): Result<Page<Post>> =
        postRepository.getUserPosts(userId, lastPostId).map { Page.of(it) }

    suspend fun search(query: String, lastPostId: Int?): Result<Page<Post>> =
        postRepository.search(query, lastPostId).map { Page.of(it) }

    suspend fun getPostDetail(postId: Int): Result<Post> = postRepository.getPost(postId)

    suspend fun uploadPost(title: String, description: String?, audioId: Int, imageId: Int?): Result<Post> =
        postRepository.createPost(title, description, audioId, imageId)

    suspend fun editPost(postId: Int, title: String, description: String?): Result<Post> =
        postRepository.updatePost(postId, title, description)

    suspend fun deletePost(postId: Int): Result<Unit> = postRepository.deletePost(postId)
}
