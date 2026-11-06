package com.neatplex.nightell.data.local.prefs

import android.content.Context
import androidx.core.content.edit
import com.neatplex.nightell.domain.repository.SessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keeps the session in SharedPreferences. The file and key names are unchanged
 * from the original TokenManager, so users stay signed in after the update.
 */
@Singleton
class SessionRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
) : SessionRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _token = MutableStateFlow(prefs.getString(KEY_TOKEN, null))
    override val token: StateFlow<String?> = _token.asStateFlow()

    private val _sessionExpired = MutableStateFlow(false)
    override val sessionExpired: StateFlow<Boolean> = _sessionExpired.asStateFlow()

    override fun saveSession(token: String, userId: Int, email: String) {
        prefs.edit {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
        }
        _token.value = token
        _sessionExpired.value = false
    }

    override fun currentUserId(): Int? = prefs.getInt(KEY_USER_ID, 0).takeIf { it != 0 }

    override fun clearSession() {
        prefs.edit { remove(KEY_TOKEN) }
        _token.value = null
    }

    override fun expireSession() {
        clearSession()
        _sessionExpired.value = true
    }

    override fun acknowledgeSessionExpired() {
        _sessionExpired.value = false
    }

    private companion object {
        const val PREFS_NAME = "token_preference"
        const val KEY_TOKEN = "auth_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_EMAIL = "user_email"
    }
}
