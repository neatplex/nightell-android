package com.neatplex.nightell.data.mapper

import com.neatplex.nightell.data.local.db.SavedPostEntity
import com.neatplex.nightell.data.remote.ApiConfig
import com.neatplex.nightell.domain.model.SavedPost

fun SavedPostEntity.toDomain() = SavedPost(
    id = id,
    title = postTitle,
    ownerName = postOwner,
    imageUrl = postImagePath?.let(ApiConfig::fileUrl),
)

fun SavedPost.toEntity() = SavedPostEntity(
    id = id,
    postTitle = title,
    postOwner = ownerName,
    postImagePath = imageUrl,
)
