package com.neatplex.nightell.dto

import com.neatplex.nightell.model.User


data class AuthResponse(
    val token: String,
    val user: User
)
