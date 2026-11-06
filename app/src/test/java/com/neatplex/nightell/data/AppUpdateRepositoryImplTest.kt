package com.neatplex.nightell.data

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.data.repository.AppUpdateRepositoryImpl
import com.neatplex.nightell.domain.model.VersionPolicy
import com.neatplex.nightell.domain.util.Result
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

class AppUpdateRepositoryImplTest {

    private val server = ApiTestServer()
    private val repository = AppUpdateRepositoryImpl(server.api())

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `reads the android versions from the settings endpoint`() = runTest {
        server.enqueue(200, """{"android_last_version": "1.3.0", "android_last_supported_version": "1.2.0"}""")

        val result = repository.getVersionPolicy()

        assertThat(result).isEqualTo(Result.Success(VersionPolicy("1.3.0", "1.2.0")))
        assertThat(server.server.takeRequest().path).isEqualTo("/api/v1/settings")
    }

    @Test
    fun `missing fields become null`() = runTest {
        server.enqueue(200, """{}""")

        assertThat(repository.getVersionPolicy()).isEqualTo(Result.Success(VersionPolicy(null, null)))
    }

    @Test
    fun `a server error is a failure`() = runTest {
        server.enqueue(500)

        assertThat(repository.getVersionPolicy()).isInstanceOf(Result.Failure::class.java)
    }
}
