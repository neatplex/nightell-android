package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LogoutViewModel @Inject constructor() : ViewModel() {
    private val _logoutEvent = MutableLiveData<Unit>()
    val logoutEvent: LiveData<Unit> = _logoutEvent

    fun triggerLogout() {
        _logoutEvent.postValue(Unit)
    }
}
