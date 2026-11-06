package com.neatplex.nightell.domain

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.model.UpdateStatus
import com.neatplex.nightell.domain.model.VersionPolicy
import com.neatplex.nightell.domain.usecase.AppUpdateUseCase
import com.neatplex.nightell.testutil.FakeAppInfo
import com.neatplex.nightell.testutil.FakeAppUpdateRepository
import com.neatplex.nightell.domain.util.Result
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AppUpdateUseCaseTest {

    private val repository = FakeAppUpdateRepository()

    private fun useCase(installed: String) = AppUpdateUseCase(repository, FakeAppInfo(installed))

    private fun policy(latest: String?, minSupported: String?) {
        repository.result = Result.Success(VersionPolicy(latest, minSupported))
    }

    @Test
    fun `an unsupported version must update`() = runTest {
        policy(latest = "1.3.0", minSupported = "1.2.0")

        assertThat(useCase("1.1.0").checkForUpdate()).isEqualTo(UpdateStatus.Required("1.2.0"))
    }

    @Test
    fun `the oldest supported version itself is allowed`() = runTest {
        policy(latest = "1.3.0", minSupported = "1.2.0")

        assertThat(useCase("1.2.0").checkForUpdate()).isEqualTo(UpdateStatus.Available("1.3.0"))
    }

    @Test
    fun `the latest version is up to date`() = runTest {
        policy(latest = "1.3.0", minSupported = "1.2.0")

        assertThat(useCase("1.3.0").checkForUpdate()).isEqualTo(UpdateStatus.UpToDate)
    }

    @Test
    fun `a version newer than the server knows is up to date`() = runTest {
        policy(latest = "1.0.7", minSupported = "1.0.0")

        assertThat(useCase("1.1.0").checkForUpdate()).isEqualTo(UpdateStatus.UpToDate)
    }

    @Test
    fun `a failed check never blocks the app`() = runTest {
        repository.result = Result.Failure("offline")

        assertThat(useCase("0.1.0").checkForUpdate()).isEqualTo(UpdateStatus.UpToDate)
    }

    @Test
    fun `missing or unreadable versions are ignored`() = runTest {
        policy(latest = null, minSupported = "soon")

        assertThat(useCase("1.0.0").checkForUpdate()).isEqualTo(UpdateStatus.UpToDate)
    }
}
