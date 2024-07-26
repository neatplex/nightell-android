package com.neatplex.nightell.domain.model

data class Like (
    val id : Int,
    val user: User,
    val created_at : String,
    val user_id : Int,
    val post_id : Int,
    val post: Post
)