package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest

interface AuthRepository {
    suspend fun register(request: RegistrationRequest): Result<AuthResponse?>
    suspend fun loginWithEmail(request: LoginEmailRequest): Result<AuthResponse?>
    suspend fun loginWithUsername(request: LoginUsernameRequest): Result<AuthResponse?>
}