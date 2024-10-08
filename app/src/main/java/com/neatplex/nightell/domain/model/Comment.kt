package com.neatplex.nightell.domain.model

data class Comment(
    val id: Int,
    val user: User,
    val postId: Int,
    val text: String,
    val createdAt: String,
)
