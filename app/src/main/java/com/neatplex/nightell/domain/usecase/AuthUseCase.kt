package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.domain.repository.AuthRepository
import com.neatplex.nightell.utils.TokenManager
import javax.inject.Inject
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.Validation


class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val validation: Validation
) {

    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        val request = RegistrationRequest(username, email, password)
        val result = authRepository.register(request)
        if (result is Result.Success) {
            result.data?.let {
                saveUserInfo(it.token, it.user.id, it.user.email)
            }
        }
        return result
    }

    suspend fun login(emailOrUsername: String, password: String): Result<AuthResponse> {
        val result = if (validation.isValidEmail(emailOrUsername)) {
            val request = LoginEmailRequest(emailOrUsername, password)
            authRepository.loginWithEmail(request)
        } else {
            val request = LoginUsernameRequest(emailOrUsername, password)
            authRepository.loginWithUsername(request)
        }

        if (result is Result.Success) {
            result.data?.let {
                saveUserInfo(it.token, it.user.id, it.user.email)
            }
        }
        return result
    }

    suspend fun signInWithGoogle(idToken: String): Result<AuthResponse> {
        val result = authRepository.signInWithGoogle(idToken)
        if (result is Result.Success) {
            result.data?.let {
                saveUserInfo(it.token, it.user.id, it.user.email)
            }
        }
        return result
    }

    private fun saveUserInfo(token: String, id: Int, email: String) {
        tokenManager.setToken(token)
        tokenManager.setId(id)
        tokenManager.setEmail(email)
    }
}