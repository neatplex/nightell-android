package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.IUserRepository
import com.neatplex.nightell.utils.Result
import javax.inject.Inject

class UserUseCase @Inject constructor(private val userRepository: IUserRepository) {

    suspend fun profile() = userRepository.fetchProfile()

    suspend fun getUserProfile(userId: Int) = userRepository.showUserProfile(userId)

    suspend fun changeProfileName(name: String) = userRepository.changeProfileName(name)

    suspend fun changeProfileBio(bio: String) = userRepository.changeProfileBio(bio)

    suspend fun changeProfileUsername(username: String) = userRepository.changeProfileUsername(username)

    suspend fun deleteAccount() = userRepository.deleteAccount()

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