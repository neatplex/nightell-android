package com.neatplex.nightell.data.dto

import com.neatplex.nightell.domain.model.User


data class AuthResponse(
    val token: String,
    val user: User
)
