package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor() : ViewModel() {
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> get() = _logoutEvent

    fun triggerLogout() {
        _logoutEvent.tryEmit(Unit)
    }
}