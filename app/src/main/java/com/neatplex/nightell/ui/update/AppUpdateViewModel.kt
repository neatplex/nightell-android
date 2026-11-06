package com.neatplex.nightell.ui.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.UpdateStatus
import com.neatplex.nightell.domain.usecase.AppUpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Activity-scoped: checks whether the installed version must or may be updated.
 *
 * [status] is [UpdateStatus.Required] while the version is no longer supported,
 * and [UpdateStatus.Available] while a newer version exists that the user hasn't
 * dismissed yet.
 */
@HiltViewModel
class AppUpdateViewModel @Inject constructor(
    private val appUpdateUseCase: AppUpdateUseCase,
) : ViewModel() {

    private val _status = MutableStateFlow<UpdateStatus>(UpdateStatus.UpToDate)
    val status: StateFlow<UpdateStatus> = _status.asStateFlow()

    /** The version the user said "Later" to, so they aren't asked again about it. */
    private var dismissedVersion: String? = null
    private var checkJob: Job? = null

    /** Asks the server again. Called each time the app comes to the foreground. */
    fun check() {
        if (checkJob?.isActive == true) return
        checkJob = viewModelScope.launch {
            val result = appUpdateUseCase.checkForUpdate()
            _status.value = when {
                result is UpdateStatus.Available && result.latestVersion == dismissedVersion -> UpdateStatus.UpToDate
                // Once required, a failed later check (offline) must not unlock the app.
                _status.value is UpdateStatus.Required && result == UpdateStatus.UpToDate -> _status.value
                else -> result
            }
        }
    }

    /** The user chose to update later. A required update can't be dismissed. */
    fun dismissAvailableUpdate() {
        val current = _status.value as? UpdateStatus.Available ?: return
        dismissedVersion = current.latestVersion
        _status.value = UpdateStatus.UpToDate
    }
}
