package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.model.AuthSession
import com.neatplex.nightell.domain.repository.AuthRepository
import com.neatplex.nightell.domain.repository.SessionRepository
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.Validator
import com.neatplex.nightell.domain.util.onSuccess
import javax.inject.Inject

/** Signs the user in or up, and stores the session when it succeeds. */
class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
    private val validator: Validator,
) {

    suspend fun register(username: String, email: String, password: String): Result<AuthSession> =
        authRepository.register(username, email, password).onSuccess(::saveSession)

    /** Accepts either an email address or a username as the identifier. */
    suspend fun login(emailOrUsername: String, password: String): Result<AuthSession> {
        val result = if (validator.isValidEmail(emailOrUsername)) {
            authRepository.loginWithEmail(emailOrUsername, password)
        } else {
            authRepository.loginWithUsername(emailOrUsername, password)
        }
        return result.onSuccess(::saveSession)
    }

    suspend fun signInWithGoogle(idToken: String): Result<AuthSession> =
        authRepository.signInWithGoogle(idToken).onSuccess(::saveSession)

    suspend fun sendOtp(email: String): Result<Int> = authRepository.sendOtp(email)

    suspend fun verifyOtp(email: String, otp: String): Result<AuthSession> =
        authRepository.verifyOtp(email, otp).onSuccess(::saveSession)

    private fun saveSession(session: AuthSession) {
        sessionRepository.saveSession(session.token, session.user.id, session.user.email)
    }
}
