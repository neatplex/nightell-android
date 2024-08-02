package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.domain.repository.IProfileRepository
import com.neatplex.nightell.utils.Result
import javax.inject.Inject

class ProfileUseCase @Inject constructor(private val profileRepository: IProfileRepository) {

    suspend fun profile(): Result<Profile> {
        return profileRepository.fetchProfile()
    }

    suspend fun getUserProfile(userId: Int): Result<Profile> {
        return profileRepository.showUserProfile(userId)
    }

    suspend fun changeProfileName(name: String): Result<UserUpdated> {
        return profileRepository.changeProfileName(name)
    }

    suspend fun changeProfileBio(bio: String): Result<UserUpdated> {
        return profileRepository.changeProfileBio(bio)
    }

    suspend fun changeProfileUsername(username: String): Result<UserUpdated> {
        return profileRepository.changeProfileUsername(username)
    }

    suspend fun deleteAccount(): Result<Unit> {
        return profileRepository.deleteAccount()
    }
}