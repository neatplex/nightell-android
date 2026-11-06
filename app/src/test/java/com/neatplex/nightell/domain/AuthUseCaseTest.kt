package com.neatplex.nightell.domain

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.model.AuthSession
import com.neatplex.nightell.domain.usecase.AuthUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.Validator
import com.neatplex.nightell.testutil.FakeAuthRepository
import com.neatplex.nightell.testutil.FakeSessionRepository
import com.neatplex.nightell.testutil.user
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AuthUseCaseTest {

    private val authRepository = FakeAuthRepository()
    private val sessionRepository = FakeSessionRepository()
    private val useCase = AuthUseCase(authRepository, sessionRepository, Validator())

    @Test
    fun `login with an email uses the email endpoint`() = runTest {
        authRepository.result = Result.Success(AuthSession("t", user()))

        useCase.login("someone@example.com", "password")

        assertThat(authRepository.calls).containsExactly("email:someone@example.com")
    }

    @Test
    fun `login with a username uses the username endpoint`() = runTest {
        authRepository.result = Result.Success(AuthSession("t", user()))

        useCase.login("night_owl", "password")

        assertThat(authRepository.calls).containsExactly("username:night_owl")
    }

    @Test
    fun `successful login saves the session`() = runTest {
        authRepository.result = Result.Success(AuthSession("token-1", user(id = 7, email = "a@b.co")))

        useCase.login("a@b.co", "password")

        assertThat(sessionRepository.token.value).isEqualTo("token-1")
        assertThat(sessionRepository.currentUserId()).isEqualTo(7)
        assertThat(sessionRepository.savedEmail).isEqualTo("a@b.co")
    }

    @Test
    fun `failed login saves nothing and keeps the server error`() = runTest {
        authRepository.result = Result.Failure("Wrong password", code = 401)

        val result = useCase.login("a@b.co", "password")

        assertThat(result).isEqualTo(Result.Failure("Wrong password", code = 401))
        assertThat(sessionRepository.token.value).isNull()
    }

    @Test
    fun `verifying an otp saves the session`() = runTest {
        authRepository.result = Result.Success(AuthSession("otp-token", user(id = 3)))

        useCase.verifyOtp("a@b.co", "123456")

        assertThat(sessionRepository.token.value).isEqualTo("otp-token")
    }
}
