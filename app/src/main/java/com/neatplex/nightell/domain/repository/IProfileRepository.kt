package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.utils.Result

interface IProfileRepository {
    suspend fun showUserProfile(userId: Int): Result<Profile>

    suspend fun fetchProfile(): Result<Profile>

    suspend fun changeProfileName(name: String): Result<UserUpdated>

    suspend fun changeProfileBio(bio: String): Result<UserUpdated>

    suspend fun changeProfileUsername(username: String): Result<UserUpdated>

    suspend fun deleteAccount(): Result<Unit>
}