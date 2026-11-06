package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.model.UpdateStatus
import com.neatplex.nightell.domain.repository.AppInfo
import com.neatplex.nightell.domain.repository.AppUpdateRepository
import com.neatplex.nightell.domain.util.AppVersion
import com.neatplex.nightell.domain.util.Result
import javax.inject.Inject

class AppUpdateUseCase @Inject constructor(
    private val repository: AppUpdateRepository,
    private val appInfo: AppInfo,
) {

    /**
     * Compares the installed version with what the server wants.
     *
     * When the check can't be made (offline, server error, a version that
     * doesn't parse) the result is [UpdateStatus.UpToDate], so a failed check
     * never locks anyone out of the app.
     */
    suspend fun checkForUpdate(): UpdateStatus {
        val installed = AppVersion.parse(appInfo.versionName) ?: return UpdateStatus.UpToDate
        val policy = (repository.getVersionPolicy() as? Result.Success)?.data
            ?: return UpdateStatus.UpToDate

        val minSupported = AppVersion.parse(policy.minSupportedVersion)
        if (minSupported != null && installed < minSupported) {
            return UpdateStatus.Required(minSupported.toString())
        }
        val latest = AppVersion.parse(policy.latestVersion)
        if (latest != null && installed < latest) {
            return UpdateStatus.Available(latest.toString())
        }
        return UpdateStatus.UpToDate
    }
}
