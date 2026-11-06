package com.neatplex.nightell.domain.model

/** Which app versions the server currently wants, e.g. "1.2.0". */
data class VersionPolicy(
    /** The newest version available in the store. */
    val latestVersion: String?,
    /** Versions older than this can no longer be used. */
    val minSupportedVersion: String?,
)

/** Whether the installed app should be updated. */
sealed interface UpdateStatus {
    data object UpToDate : UpdateStatus

    /** A newer version exists; the user may update now or later. */
    data class Available(val latestVersion: String) : UpdateStatus

    /** This version is no longer supported; the user must update to continue. */
    data class Required(val minSupportedVersion: String) : UpdateStatus
}
