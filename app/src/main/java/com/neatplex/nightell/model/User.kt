package com.neatplex.nightell.model


data class User(
    val bio: String,
    val created_at: String,
    val email: String,
    val id: Int,
    val is_banned: Boolean,
    val name: String,
    val password: String,
    val username: String
)
