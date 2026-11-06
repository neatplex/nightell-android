package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.model.Profile
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.model.UserProfile
import com.neatplex.nightell.domain.repository.SessionRepository
import com.neatplex.nightell.domain.repository.UserRepository
import com.neatplex.nightell.domain.util.Page
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.map
import com.neatplex.nightell.domain.util.onSuccess
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
) {

    suspend fun profile(): Result<Profile> = userRepository.getMyProfile()

    suspend fun getUserProfile(userId: Int): Result<UserProfile> = userRepository.getUserProfile(userId)

    suspend fun changeProfileName(name: String): Result<User> = userRepository.updateName(name)

    suspend fun changeProfileBio(bio: String): Result<User> = userRepository.updateBio(bio)

    suspend fun changeProfileImage(imageId: Int): Result<User> = userRepository.updateImage(imageId)

    suspend fun changeProfileUsername(username: String): Result<User> =
        userRepository.updateUsername(username)

    /** Deletes the account on the server, then signs out locally. */
    suspend fun deleteAccount(): Result<Unit> =
        userRepository.deleteAccount().onSuccess { sessionRepository.clearSession() }

    suspend fun searchUser(query: String, lastId: Int?): Result<Page<User>> =
        userRepository.searchUsers(query, lastId).map { Page.of(it) }
}
