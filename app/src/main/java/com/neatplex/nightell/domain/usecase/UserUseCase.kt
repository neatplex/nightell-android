package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.UserResponse
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.IUserRepository
import com.neatplex.nightell.utils.Result
import javax.inject.Inject

class UserUseCase @Inject constructor(private val userRepository: IUserRepository) {

    suspend fun profile(): Result<Profile> {
        return userRepository.fetchProfile()
    }

    suspend fun getUserProfile(userId: Int): Result<UserResponse> {
        return userRepository.showUserProfile(userId)
    }

    suspend fun changeProfileName(name: String): Result<UserUpdated> {
        return userRepository.changeProfileName(name)
    }

    suspend fun changeProfileBio(bio: String): Result<UserUpdated> {
        return userRepository.changeProfileBio(bio)
    }

    suspend fun changeProfileUsername(username: String): Result<UserUpdated> {
        return userRepository.changeProfileUsername(username)
    }

    suspend fun deleteAccount(): Result<Unit> {
        return userRepository.deleteAccount()
    }

    suspend fun searchUser(query: String, lastId: Int?): Result<List<User>?> {
        val result = userRepository.searchUser(query, lastId)
        return if (result is Result.Success) {
            val newFeed = result.data?.users ?: emptyList()
            Result.Success(newFeed)
        } else {
            Result.Failure("Error loading searched users", null)
        }
    }
}