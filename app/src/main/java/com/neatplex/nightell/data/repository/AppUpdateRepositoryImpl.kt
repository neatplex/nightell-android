package com.neatplex.nightell.data.repository

import com.neatplex.nightell.data.remote.api.ApiService
import com.neatplex.nightell.data.remote.apiCall
import com.neatplex.nightell.domain.model.VersionPolicy
import com.neatplex.nightell.domain.repository.AppUpdateRepository
import com.neatplex.nightell.domain.util.Result
import javax.inject.Inject

class AppUpdateRepositoryImpl @Inject constructor(private val api: ApiService) : AppUpdateRepository {

    override suspend fun getVersionPolicy(): Result<VersionPolicy> =
        apiCall({ api.getSettings() }) {
            VersionPolicy(
                latestVersion = it.androidLastVersion,
                minSupportedVersion = it.androidLastSupportedVersion,
            )
        }
}
