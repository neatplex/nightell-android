package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.PostStoreResponse
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.repository.PostRepository
import com.neatplex.nightell.utils.Result
import javax.inject.Inject

class PostUseCase @Inject constructor(private val postRepository: PostRepository) {

    private var lastPostId: Int? = null
    private var lastUserPostId: Int? = null
    private var lastSearchedPostId: Int? = null

    private var allPosts = emptyList<Post>()
    private var allUserPosts = emptyList<Post>()
    private var allSearchedPosts = emptyList<Post>()

    suspend fun loadFeed() : Result<List<Post>> {
        val result = postRepository.showFeed(lastPostId)
        return if (result is Result.Success) {
            val newFeed = result.data?.posts ?: emptyList()
            if (newFeed.isNotEmpty()) {
                lastPostId = newFeed.last().id
                allPosts = newFeed + allPosts
            }
            Result.Success(allPosts)
        } else {
            Result.Error("Error loading feed", null)
        }
    }

    suspend fun refreshFeed() : Result<List<Post>> {
        lastPostId = null
        allPosts = emptyList()
        return loadFeed() // Reload the feed
    }

    suspend fun loadUserPosts(userId: Int): Result<List<Post>?> {
        val result = postRepository.showUserPosts(userId, lastUserPostId)
        return if (result is Result.Success) {
            val newFeed = result.data?.posts ?: emptyList()
            if (newFeed.isNotEmpty()) {
                lastUserPostId = newFeed.last().id
                allUserPosts = newFeed + allPosts
            }
            Result.Success(allUserPosts)
        } else {
            Result.Error("Error loading user posts", null)
        }
    }

    suspend fun uploadPost(title: String, description: String?, audioId: Int, imageId: Int?): Result<PostStoreResponse> {
        return postRepository.uploadPost(title, description, audioId, imageId)
    }

    suspend fun editPost(postId: Int, newTitle: String, newDescription: String): Result<PostStoreResponse> {
        return postRepository.editPost(newTitle, newDescription, postId)
    }

    suspend fun deletePost(postId: Int): Result<Unit> {
        return postRepository.deletePost(postId)
    }

    suspend fun search(query: String) : Result<List<Post>> {
        val result = postRepository.search(query,lastSearchedPostId)
        return if (result is Result.Success) {
            allSearchedPosts = result.data?.posts ?: emptyList()
            Result.Success(allSearchedPosts)
        } else {
            Result.Error("Error loading searched posts", null)
        }
    }
}