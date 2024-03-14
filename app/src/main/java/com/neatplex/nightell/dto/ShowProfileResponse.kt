package com.neatplex.nightell.dto

import com.neatplex.nightell.model.User

data class ShowProfileResponse (
    val followers_count : Int,
    val followings_count : Int,
    val user : User
)