package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.neatplex.nightell.domain.repository.ConnectivityMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    connectivityMonitor: ConnectivityMonitor,
) : ViewModel() {
    val isOnline: StateFlow<Boolean> = connectivityMonitor.isOnline
}
