package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.SavedPost
import com.neatplex.nightell.domain.repository.SavedPostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Bookmarks stored on this device. */
class SavedPostUseCase @Inject constructor(private val savedPostRepository: SavedPostRepository) {

    fun observeSavedPosts(): Flow<List<SavedPost>> = savedPostRepository.observeSavedPosts()

    suspend fun isSaved(postId: Int): Boolean = savedPostRepository.isSaved(postId)

    suspend fun save(post: Post) {
        savedPostRepository.save(
            SavedPost(
                id = post.id,
                title = post.title,
                ownerName = post.user.username,
                imageUrl = post.image?.url,
            )
        )
    }

    suspend fun remove(postId: Int) = savedPostRepository.remove(postId)
}
