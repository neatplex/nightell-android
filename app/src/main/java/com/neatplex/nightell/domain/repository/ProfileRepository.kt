package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.ShowProfileResponse
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.utils.Result

interface ProfileRepository {
    suspend fun showUserProfile(userId: Int): Result<ShowProfileResponse>

    suspend fun profile(): Result<ShowProfileResponse>

    suspend fun changeProfileName(name: String): Result<UserUpdated>

    suspend fun changeProfileBio(bio: String): Result<UserUpdated>

    suspend fun changeProfileUsername(username: String): Result<UserUpdated>
}