package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.PostDetailResponse
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.repository.IPostRepository
import com.neatplex.nightell.utils.Result
import javax.inject.Inject

class PostUseCase @Inject constructor(private val postRepository: IPostRepository) {

    suspend fun loadFeed(lastPostId: Int?) : Result<List<Post>> {
        val result = postRepository.showFeed(lastPostId)
        return if (result is Result.Success) {
            val newFeed = result.data?.posts ?: emptyList()
            Result.Success(newFeed)
        } else {
            Result.Failure("Error loading feed", null)
        }
    }

    suspend fun loadUserPosts(userId: Int, lastUserPostId: Int?): Result<List<Post>?> {
        val result = postRepository.showUserPosts(userId, lastUserPostId)
        return if (result is Result.Success) {
            val newFeed = result.data?.posts ?: emptyList()
            Result.Success(newFeed)
        } else {
            Result.Failure("Error loading feed", null)
        }
    }

    suspend fun uploadPost(title: String, description: String?, audioId: Int, imageId: Int?): Result<PostDetailResponse> {
        return postRepository.uploadPost(title, description, audioId, imageId)
    }

    suspend fun editPost(postId: Int, newTitle: String, newDescription: String)= postRepository.editPost(newTitle, newDescription, postId)

    suspend fun deletePost(postId: Int) = postRepository.deletePost(postId)

    suspend fun search(query: String, lastPostId: Int?) : Result<List<Post>> {
        val result = postRepository.search(query,lastPostId)
        return if (result is Result.Success) {
            val newFeed = result.data?.posts ?: emptyList()
            Result.Success(newFeed)
        } else {
            Result.Failure("Error loading searched posts", null)
        }
    }

    suspend fun getPostDetail(postId: Int) = postRepository.getPostById(postId)
}