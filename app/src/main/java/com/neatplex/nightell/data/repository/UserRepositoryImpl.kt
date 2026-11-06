package com.neatplex.nightell.data.repository

import com.neatplex.nightell.data.mapper.toDomain
import com.neatplex.nightell.data.remote.api.ApiService
import com.neatplex.nightell.data.remote.apiCall
import com.neatplex.nightell.data.remote.apiCallNoContent
import com.neatplex.nightell.data.remote.dto.BioUpdateRequest
import com.neatplex.nightell.data.remote.dto.ImageUpdateRequest
import com.neatplex.nightell.data.remote.dto.NameUpdateRequest
import com.neatplex.nightell.data.remote.dto.UsernameUpdateRequest
import com.neatplex.nightell.domain.model.Profile
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.model.UserProfile
import com.neatplex.nightell.domain.repository.UserRepository
import com.neatplex.nightell.domain.util.Result
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val api: ApiService) : UserRepository {

    override suspend fun getMyProfile(): Result<Profile> =
        apiCall({ api.showProfile() }) { it.toDomain() }

    override suspend fun getUserProfile(userId: Int): Result<UserProfile> =
        apiCall({ api.showUserProfile(userId) }) { it.toDomain() }

    override suspend fun updateName(name: String): Result<User> =
        apiCall({ api.changeProfileName(NameUpdateRequest(name)) }) { it.user.toDomain() }

    override suspend fun updateBio(bio: String): Result<User> =
        apiCall({ api.changeProfileBio(BioUpdateRequest(bio)) }) { it.user.toDomain() }

    override suspend fun updateUsername(username: String): Result<User> =
        apiCall({ api.changeProfileUsername(UsernameUpdateRequest(username)) }) { it.user.toDomain() }

    override suspend fun updateImage(imageId: Int): Result<User> =
        apiCall({ api.changeProfileImage(ImageUpdateRequest(imageId)) }) { it.user.toDomain() }

    override suspend fun deleteAccount(): Result<Unit> = apiCallNoContent { api.deleteAccount() }

    override suspend fun searchUsers(query: String, lastId: Int?): Result<List<User>> =
        apiCall({ api.searchUser(query, lastId) }) { response ->
            response.users.orEmpty().map { it.toDomain() }
        }
}
