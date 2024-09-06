package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.UserResponse
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.utils.Result

interface IUserRepository {
    suspend fun showUserProfile(userId: Int): Result<UserResponse>

    suspend fun fetchProfile(): Result<Profile>

    suspend fun changeProfileName(name: String): Result<UserUpdated>

    suspend fun changeProfileBio(bio: String): Result<UserUpdated>

    suspend fun changeProfileImage(id: Int): Result<UserUpdated>

    suspend fun changeProfileUsername(username: String): Result<UserUpdated>

    suspend fun deleteAccount(): Result<Unit>

    suspend fun searchUser(query: String, lastId: Int?): Result<Users>
}