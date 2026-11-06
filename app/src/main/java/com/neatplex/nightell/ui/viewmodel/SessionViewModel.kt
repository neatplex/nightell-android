package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.SessionRepository
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Activity-scoped state about who is signed in. Screens read the current user
 * from here instead of passing it around.
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userUseCase: UserUseCase,
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val token: StateFlow<String?> = sessionRepository.token

    /** True when the server rejected the token; the UI should send the user to sign in. */
    val sessionExpired: StateFlow<Boolean> = sessionRepository.sessionExpired

    val isSignedIn: Boolean get() = sessionRepository.token.value != null

    fun refreshCurrentUser() {
        viewModelScope.launch {
            val result = userUseCase.profile()
            if (result is Result.Success) _currentUser.value = result.data.user
        }
    }

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    /** Call once the UI has reacted to [sessionExpired], so it doesn't fire again. */
    fun onSessionExpiredHandled() = sessionRepository.acknowledgeSessionExpired()

    fun signOut() {
        sessionRepository.clearSession()
        _currentUser.value = null
    }
}
