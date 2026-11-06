package com.neatplex.nightell.ui

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.testutil.FakeSessionRepository
import com.neatplex.nightell.testutil.FakeUserRepository
import com.neatplex.nightell.testutil.MainDispatcherRule
import com.neatplex.nightell.testutil.user
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import com.neatplex.nightell.ui.viewmodel.SignOutReason
import org.junit.Rule
import org.junit.Test

class SessionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(session: FakeSessionRepository, users: FakeUserRepository = FakeUserRepository()) =
        SessionViewModel(session, UserUseCase(users, session))

    @Test
    fun `signing out clears the session and asks the app to restart`() {
        val session = FakeSessionRepository(token = "t", userId = 1)
        val viewModel = viewModel(session)
        viewModel.setCurrentUser(user(1))

        viewModel.signOut()

        assertThat(session.token.value).isNull()
        assertThat(viewModel.currentUser.value).isNull()
        assertThat(viewModel.signOutReason.value).isEqualTo(SignOutReason.SIGNED_OUT)
    }

    @Test
    fun `deleting the account elsewhere also triggers the restart`() {
        val session = FakeSessionRepository(token = "t", userId = 1)
        val users = FakeUserRepository()
        val viewModel = viewModel(session, users)

        // What EditProfileViewModel does: the use case clears the session on success.
        kotlinx.coroutines.runBlocking { UserUseCase(users, session).deleteAccount() }

        assertThat(viewModel.signOutReason.value).isEqualTo(SignOutReason.SIGNED_OUT)
    }

    @Test
    fun `a rejected token is reported as an expired session`() {
        val session = FakeSessionRepository(token = "t", userId = 1)
        val viewModel = viewModel(session)

        session.expireSession()

        assertThat(viewModel.signOutReason.value).isEqualTo(SignOutReason.SESSION_EXPIRED)
    }

    @Test
    fun `starting signed out does not trigger a restart`() {
        val session = FakeSessionRepository(token = null)

        val viewModel = viewModel(session)

        assertThat(viewModel.signOutReason.value).isNull()
    }

    @Test
    fun `signing in later does not trigger a restart`() {
        val session = FakeSessionRepository(token = null)
        val viewModel = viewModel(session)

        session.saveSession("t", 1, "a@b.co")

        assertThat(viewModel.signOutReason.value).isNull()
    }

    @Test
    fun `a failed account deletion keeps the user signed in`() {
        val session = FakeSessionRepository(token = "t", userId = 1)
        val users = FakeUserRepository().apply { deleteResult = Result.Failure("nope", code = 500) }
        val viewModel = viewModel(session, users)

        kotlinx.coroutines.runBlocking { UserUseCase(users, session).deleteAccount() }

        assertThat(viewModel.signOutReason.value).isNull()
    }
}
