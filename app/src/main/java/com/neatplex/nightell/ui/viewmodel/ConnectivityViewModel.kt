package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.neatplex.nightell.domain.repository.ConnectivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectivityViewModel @Inject constructor(connectivityRepository: ConnectivityRepository) : ViewModel() {

    val isOnline = connectivityRepository.isConnected.asLiveData()
}