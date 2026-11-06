package com.neatplex.nightell.domain

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.testutil.FakeSessionRepository
import com.neatplex.nightell.testutil.FakeUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UserUseCaseTest {

    private val userRepository = FakeUserRepository()
    private val sessionRepository = FakeSessionRepository(token = "token", userId = 1)
    private val useCase = UserUseCase(userRepository, sessionRepository)

    @Test
    fun `deleting the account signs out`() = runTest {
        useCase.deleteAccount()

        assertThat(sessionRepository.token.value).isNull()
    }

    @Test
    fun `a failed delete keeps the session`() = runTest {
        userRepository.deleteResult = Result.Failure("nope", code = 500)

        useCase.deleteAccount()

        assertThat(sessionRepository.token.value).isEqualTo("token")
    }
}
