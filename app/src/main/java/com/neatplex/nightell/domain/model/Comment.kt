package com.neatplex.nightell.domain.model

import java.util.Date

data class Comment(
    val id: Int,
    val user: User,
    val postId: Int,
    val text: String,
    val created_at: Date,
)
