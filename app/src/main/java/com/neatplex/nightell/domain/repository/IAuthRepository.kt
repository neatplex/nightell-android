package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.OtpResponseTtl
import com.neatplex.nightell.data.dto.OtpVerifyRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    suspend fun register(request: RegistrationRequest): Result<AuthResponse>
    suspend fun loginWithEmail(request: LoginEmailRequest): Result<AuthResponse>
    suspend fun loginWithUsername(request: LoginUsernameRequest): Result<AuthResponse>
    suspend fun signInWithGoogle(idToken: String): Result<AuthResponse>
    suspend fun sendOtp(email: String): Result<OtpResponseTtl>
    suspend fun verifyOtp(request: OtpVerifyRequest): Result<AuthResponse>
}