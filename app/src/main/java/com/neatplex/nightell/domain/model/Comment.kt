package com.neatplex.nightell.domain.model

data class Comment(
    val id: Int,
    val postId: Int,
    val text: String,
)
