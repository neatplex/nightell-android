package com.neatplex.nightell.data.repository

import com.neatplex.nightell.data.mapper.toDomain
import com.neatplex.nightell.data.remote.api.ApiService
import com.neatplex.nightell.data.remote.apiCall
import com.neatplex.nightell.data.remote.apiCallNoContent
import com.neatplex.nightell.data.remote.dto.UserListResponse
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.FollowRepository
import com.neatplex.nightell.domain.util.Result
import javax.inject.Inject

class FollowRepositoryImpl @Inject constructor(private val api: ApiService) : FollowRepository {

    override suspend fun follow(userId: Int): Result<Unit> = apiCallNoContent { api.follow(userId) }

    override suspend fun unfollow(userId: Int): Result<Unit> = apiCallNoContent { api.unfollow(userId) }

    override suspend fun getFollowers(userId: Int, lastId: Int?, count: Int?): Result<List<User>> =
        apiCall({ api.userFollowers(userId, lastId, count) }, ::toUsers)

    override suspend fun getFollowings(userId: Int, lastId: Int?, count: Int?): Result<List<User>> =
        apiCall({ api.userFollowings(userId, lastId, count) }, ::toUsers)

    private fun toUsers(response: UserListResponse) = response.users.orEmpty().map { it.toDomain() }
}
