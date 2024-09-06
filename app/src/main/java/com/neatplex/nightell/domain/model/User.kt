package com.neatplex.nightell.domain.model

data class User(
    val bio: String,
    val created_at: String,
    val email: String,
    val id: Int,
    val image_id: Int?,
    val image: CustomFile?,
    val name: String,
    val password: String,
    val username: String
)
