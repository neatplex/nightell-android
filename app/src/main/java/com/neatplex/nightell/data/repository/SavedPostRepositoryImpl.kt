package com.neatplex.nightell.data.repository

import com.neatplex.nightell.data.local.db.SavedPostDao
import com.neatplex.nightell.data.mapper.toDomain
import com.neatplex.nightell.data.mapper.toEntity
import com.neatplex.nightell.domain.model.SavedPost
import com.neatplex.nightell.domain.repository.SavedPostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SavedPostRepositoryImpl @Inject constructor(
    private val dao: SavedPostDao,
) : SavedPostRepository {

    override fun observeSavedPosts(): Flow<List<SavedPost>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun isSaved(postId: Int): Boolean = dao.exists(postId)

    override suspend fun save(post: SavedPost) = dao.insert(post.toEntity())

    override suspend fun remove(postId: Int) = dao.deleteById(postId)
}
