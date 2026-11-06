package com.neatplex.nightell.ui

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.model.UpdateStatus
import com.neatplex.nightell.domain.model.VersionPolicy
import com.neatplex.nightell.domain.usecase.AppUpdateUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.testutil.FakeAppInfo
import com.neatplex.nightell.testutil.FakeAppUpdateRepository
import com.neatplex.nightell.testutil.MainDispatcherRule
import com.neatplex.nightell.ui.update.AppUpdateViewModel
import org.junit.Rule
import org.junit.Test

class AppUpdateViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAppUpdateRepository()
    private val viewModel by lazy {
        AppUpdateViewModel(AppUpdateUseCase(repository, FakeAppInfo("1.1.0")))
    }

    private fun serverSays(latest: String, minSupported: String) {
        repository.result = Result.Success(VersionPolicy(latest, minSupported))
    }

    @Test
    fun `starts up to date until checked`() {
        assertThat(viewModel.status.value).isEqualTo(UpdateStatus.UpToDate)
    }

    @Test
    fun `an unsupported version is required to update`() {
        serverSays(latest = "1.2.0", minSupported = "1.2.0")

        viewModel.check()

        assertThat(viewModel.status.value).isEqualTo(UpdateStatus.Required("1.2.0"))
    }

    @Test
    fun `a dismissed update is not offered again`() {
        serverSays(latest = "1.2.0", minSupported = "1.0.0")
        viewModel.check()
        assertThat(viewModel.status.value).isEqualTo(UpdateStatus.Available("1.2.0"))

        viewModel.dismissAvailableUpdate()
        viewModel.check()

        assertThat(viewModel.status.value).isEqualTo(UpdateStatus.UpToDate)
    }

    @Test
    fun `a newer release is offered even after dismissing the previous one`() {
        serverSays(latest = "1.2.0", minSupported = "1.0.0")
        viewModel.check()
        viewModel.dismissAvailableUpdate()

        serverSays(latest = "1.3.0", minSupported = "1.0.0")
        viewModel.check()

        assertThat(viewModel.status.value).isEqualTo(UpdateStatus.Available("1.3.0"))
    }

    @Test
    fun `a required update cannot be dismissed`() {
        serverSays(latest = "1.2.0", minSupported = "1.2.0")
        viewModel.check()

        viewModel.dismissAvailableUpdate()

        assertThat(viewModel.status.value).isEqualTo(UpdateStatus.Required("1.2.0"))
    }

    @Test
    fun `going offline does not unlock a required update`() {
        serverSays(latest = "1.2.0", minSupported = "1.2.0")
        viewModel.check()

        repository.result = Result.Failure("offline")
        viewModel.check()

        assertThat(viewModel.status.value).isEqualTo(UpdateStatus.Required("1.2.0"))
    }
}
