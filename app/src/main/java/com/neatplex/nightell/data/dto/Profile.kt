package com.neatplex.nightell.data.dto

import com.neatplex.nightell.domain.model.User

data class Profile (
    val followers_count : Int,
    val followings_count : Int,
    val user : User
)