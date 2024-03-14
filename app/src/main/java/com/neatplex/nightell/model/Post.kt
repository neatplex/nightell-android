package com.neatplex.nightell.model

data class Post(
    val audio: CustomFile,
    val audio_id: Int,
    val comments_count: Long,
    val created_at: String,
    val description: String?,
    val id: Int,
    val image: CustomFile?,
    val image_id: Int?,
    val likes: List<Like>,
    val likes_count: Int,
    val title: String,
    val user: User,
    val user_id: Int
)