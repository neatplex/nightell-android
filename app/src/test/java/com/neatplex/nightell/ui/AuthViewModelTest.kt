package com.neatplex.nightell.ui

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.model.AuthSession
import com.neatplex.nightell.domain.usecase.AuthUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.Validator
import com.neatplex.nightell.testutil.FakeAuthRepository
import com.neatplex.nightell.testutil.FakeSessionRepository
import com.neatplex.nightell.testutil.MainDispatcherRule
import com.neatplex.nightell.testutil.user
import com.neatplex.nightell.ui.screens.auth.AuthError
import com.neatplex.nightell.ui.screens.auth.AuthEvent
import com.neatplex.nightell.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository = FakeAuthRepository()
    private val viewModel = AuthViewModel(AuthUseCase(authRepository, FakeSessionRepository(), Validator()), Validator())

    @Test
    fun `successful sign-in emits SignedIn once`() = runTest {
        val session = AuthSession("t", user())
        authRepository.result = Result.Success(session)

        viewModel.loginUser("user1", "password")

        assertThat(viewModel.events.first()).isEqualTo(AuthEvent.SignedIn(session))
        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.authError).isNull()
    }

    @Test
    fun `failed sign-in keeps the error code for the screen to word`() {
        authRepository.result = Result.Failure("Unauthorized", code = 401)

        viewModel.loginUser("user1", "wrong")

        assertThat(viewModel.uiState.value.authError).isEqualTo(AuthError("Unauthorized", 401))
    }

    @Test
    fun `sending an otp emits OtpSent`() = runTest {
        viewModel.sendOtp("a@b.co")

        assertThat(viewModel.events.first()).isEqualTo(AuthEvent.OtpSent)
    }
}
