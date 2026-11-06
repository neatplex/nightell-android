package com.neatplex.nightell.data

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.data.remote.apiCall
import com.neatplex.nightell.data.repository.LikeRepositoryImpl
import com.neatplex.nightell.data.repository.PostRepositoryImpl
import com.neatplex.nightell.domain.util.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

/** Error handling shared by every repository, exercised through real HTTP responses. */
class ApiCallTest {

    private val server = ApiTestServer()
    private val repository = PostRepositoryImpl(server.api())

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `a 4xx failure carries the server's message and code`() = runTest {
        server.enqueue(422, """{"message": "Title is required"}""")

        val result = repository.getPost(1)

        assertThat(result).isEqualTo(Result.Failure("Title is required", code = 422))
    }

    @Test
    fun `a 4xx failure without a message falls back to a generic one`() = runTest {
        server.enqueue(404, "not json")

        val result = repository.getPost(1) as Result.Failure

        assertThat(result.code).isEqualTo(404)
        assertThat(result.message).isEqualTo("Error occurred, please try again")
    }

    @Test
    fun `a 5xx failure is reported as a server error`() = runTest {
        server.enqueue(503)

        val result = repository.getPost(1) as Result.Failure

        assertThat(result.code).isEqualTo(503)
        assertThat(result.message).isEqualTo("Internal server error!")
    }

    @Test
    fun `an empty 204 counts as success for delete endpoints`() = runTest {
        server.enqueue(204)

        assertThat(LikeRepositoryImpl(server.api()).unlike(3)).isEqualTo(Result.Success(Unit))
    }

    @Test
    fun `a network error becomes a failure with the exception`() = runTest {
        val offline = OkHttpClient.Builder()
            .addInterceptor { throw IOException("No internet connection") }
            .build()

        val result = PostRepositoryImpl(server.api(offline)).getPost(1) as Result.Failure

        assertThat(result.message).isEqualTo("No internet connection")
        assertThat(result.exception).isInstanceOf(IOException::class.java)
    }

    @Test
    fun `cancellation is rethrown instead of being reported as a failure`() {
        assertThrows(CancellationException::class.java) {
            runBlocking {
                apiCall<String, String>({ throw CancellationException("cancelled") }) { it }
            }
        }
    }
}
