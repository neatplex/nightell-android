package com.neatplex.nightell.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val postTitle: String,
    val postOwner: String,
    val postImagePath: String?
)