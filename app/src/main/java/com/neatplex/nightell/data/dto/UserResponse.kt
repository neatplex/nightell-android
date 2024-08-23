package com.neatplex.nightell.data.dto

import com.neatplex.nightell.domain.model.User

data class UserResponse(
    val followed_by_me : Boolean,
    val follows_me : Boolean,
    val followers_count : Int,
    val followings_count : Int,
    val user : User
)
