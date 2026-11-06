package com.neatplex.nightell.data

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.data.remote.interceptor.AuthInterceptor
import com.neatplex.nightell.data.remote.interceptor.RetryInterceptor
import com.neatplex.nightell.testutil.FakeSessionRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.After
import org.junit.Test

class InterceptorTest {

    private val server = ApiTestServer()

    @After
    fun tearDown() = server.shutdown()

    private fun client(vararg interceptors: okhttp3.Interceptor) =
        OkHttpClient.Builder().apply { interceptors.forEach { addInterceptor(it) } }.build()

    private fun get(path: String) = Request.Builder().url(server.server.url(path)).build()

    @Test
    fun `retries a GET that hits a 5xx, then returns the success`() {
        server.enqueue(500)
        server.enqueue(200, "ok")

        val response = client(RetryInterceptor(maxRetries = 2, sleeper = {})).newCall(get("/feed")).execute()

        assertThat(response.code).isEqualTo(200)
        assertThat(server.server.requestCount).isEqualTo(2)
    }

    @Test
    fun `does not retry a 4xx such as a wrong password`() {
        server.enqueue(401)

        val response = client(RetryInterceptor(maxRetries = 2, sleeper = {})).newCall(get("/profile")).execute()

        assertThat(response.code).isEqualTo(401)
        assertThat(server.server.requestCount).isEqualTo(1)
    }

    @Test
    fun `never retries a POST, so nothing is created twice`() {
        server.enqueue(500)
        val post = Request.Builder().url(server.server.url("/posts")).post("{}".toRequestBody()).build()

        client(RetryInterceptor(maxRetries = 2, sleeper = {})).newCall(post).execute()

        assertThat(server.server.requestCount).isEqualTo(1)
    }

    @Test
    fun `stops after the retry limit`() {
        repeat(3) { server.enqueue(503) }

        val response = client(RetryInterceptor(maxRetries = 2, sleeper = {})).newCall(get("/feed")).execute()

        assertThat(response.code).isEqualTo(503)
        assertThat(server.server.requestCount).isEqualTo(3)
    }

    @Test
    fun `adds the bearer token`() {
        server.enqueue(200)
        val session = FakeSessionRepository(token = "abc")

        client(AuthInterceptor(session)).newCall(get("/api/v1/feed")).execute()

        assertThat(server.server.takeRequest().getHeader("Authorization")).isEqualTo("Bearer abc")
    }

    @Test
    fun `a 401 on a normal endpoint expires the session`() {
        server.enqueue(401)
        val session = FakeSessionRepository(token = "abc")

        client(AuthInterceptor(session)).newCall(get("/api/v1/feed")).execute()

        assertThat(session.sessionExpired.value).isTrue()
        assertThat(session.token.value).isNull()
    }

    @Test
    fun `a 401 on sign-in does not expire the session`() {
        server.enqueue(401)
        val session = FakeSessionRepository(token = null)

        client(AuthInterceptor(session)).newCall(get("/api/v1/auth/sign-in/email")).execute()

        assertThat(session.sessionExpired.value).isFalse()
    }
}
