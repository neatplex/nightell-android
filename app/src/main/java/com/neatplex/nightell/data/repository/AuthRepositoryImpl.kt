package com.neatplex.nightell.data.repository

import com.neatplex.nightell.data.mapper.toDomain
import com.neatplex.nightell.data.remote.api.ApiService
import com.neatplex.nightell.data.remote.apiCall
import com.neatplex.nightell.data.remote.dto.GoogleSignInRequest
import com.neatplex.nightell.data.remote.dto.LoginEmailRequest
import com.neatplex.nightell.data.remote.dto.LoginUsernameRequest
import com.neatplex.nightell.data.remote.dto.OtpSendRequest
import com.neatplex.nightell.data.remote.dto.OtpVerifyRequest
import com.neatplex.nightell.data.remote.dto.RegistrationRequest
import com.neatplex.nightell.domain.model.AuthSession
import com.neatplex.nightell.domain.repository.AuthRepository
import com.neatplex.nightell.domain.util.Result
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val api: ApiService) : AuthRepository {

    override suspend fun register(username: String, email: String, password: String): Result<AuthSession> =
        apiCall({ api.register(RegistrationRequest(username, email, password)) }) { it.toDomain() }

    override suspend fun loginWithEmail(email: String, password: String): Result<AuthSession> =
        apiCall({ api.loginWithEmail(LoginEmailRequest(email, password)) }) { it.toDomain() }

    override suspend fun loginWithUsername(username: String, password: String): Result<AuthSession> =
        apiCall({ api.loginWithUsername(LoginUsernameRequest(username, password)) }) { it.toDomain() }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthSession> =
        apiCall({ api.signInWithGoogle(GoogleSignInRequest(idToken)) }) { it.toDomain() }

    override suspend fun sendOtp(email: String): Result<Int> =
        apiCall({ api.sendOtp(OtpSendRequest(email)) }) { it.ttl }

    override suspend fun verifyOtp(email: String, otp: String): Result<AuthSession> =
        apiCall({ api.verifyOtp(OtpVerifyRequest(email, otp)) }) { it.toDomain() }
}
