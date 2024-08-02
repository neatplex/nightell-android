package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.utils.Result


interface IFollowRepository {
    suspend fun follow(userId: Int, friendId: Int): Result<Unit>
    suspend fun unfollow(userId: Int, friendId: Int): Result<Unit>
    suspend fun followers(userId: Int): Result<Users>
    suspend fun followings(userId: Int): Result<Users>
}