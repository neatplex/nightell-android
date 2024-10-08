package com.neatplex.nightell.data.dto

data class PostCommentRequest(
    val post_id: Int,
    val text: String,
)
