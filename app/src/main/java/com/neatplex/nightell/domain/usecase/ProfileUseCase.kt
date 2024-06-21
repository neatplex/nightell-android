package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.ShowProfileResponse
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.domain.repository.ProfileRepository
import com.neatplex.nightell.utils.Result
import javax.inject.Inject

class ProfileUseCase @Inject constructor(private val profileRepository: ProfileRepository) {

    suspend fun profile() : Result<ShowProfileResponse> {
        val result = profileRepository.profile()
        return result
    }

    suspend fun showUserProfile(userId: Int): Result<ShowProfileResponse> {
        val result = profileRepository.showUserProfile(userId)
        return result
    }

    suspend fun changeProfileName(name: String): Result<UserUpdated> {
        val result = profileRepository.changeProfileName(name)
        return result
    }

    suspend fun changeProfileBio(bio: String): Result<UserUpdated> {
        val result = profileRepository.changeProfileBio(bio)
        return result
    }

    suspend fun changeProfileUsername(username: String): Result<UserUpdated> {
        val result = profileRepository.changeProfileUsername(username)
        return result
    }

}