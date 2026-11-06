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

/** Why the user is no longer signed in. */
enum class SignOutReason {
    /** The user signed out or deleted their account. */
    SIGNED_OUT,

    /** The server rejected the token. */
    SESSION_EXPIRED,
}

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

    private val _signOutReason = MutableStateFlow<SignOutReason?>(null)

    /**
     * Set once the user stops being signed in during this session, for any
     * reason. The activity reacts by restarting into the sign-in screen, so no
     * screen has to handle it itself.
     */
    val signOutReason: StateFlow<SignOutReason?> = _signOutReason.asStateFlow()

    /** True when the server rejected the token; the UI should send the user to sign in. */
    val sessionExpired: StateFlow<Boolean> = sessionRepository.sessionExpired

    val isSignedIn: Boolean get() = sessionRepository.token.value != null

    init {
        viewModelScope.launch {
            var wasSignedIn = false
            sessionRepository.token.collect { token ->
                val signedIn = token != null
                if (wasSignedIn && !signedIn) {
                    _signOutReason.value =
                        if (sessionExpired.value) SignOutReason.SESSION_EXPIRED else SignOutReason.SIGNED_OUT
                }
                wasSignedIn = signedIn
            }
        }
    }

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
