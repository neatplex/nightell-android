package com.neatplex.nightell.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.math.pow

/**
 * Retries a request that failed for a transient reason.
 *
 * Only idempotent methods are retried, so a create request (POST) is never sent
 * twice. Only network errors and 5xx/429 responses are retried; a 4xx such as a
 * wrong password is returned straight away.
 */
class RetryInterceptor(
    private val maxRetries: Int,
    private val sleeper: (Long) -> Unit = Thread::sleep,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.method !in IDEMPOTENT_METHODS) return chain.proceed(request)

        var attempt = 0
        while (true) {
            try {
                val response = chain.proceed(request)
                if (!isRetryable(response.code) || attempt >= maxRetries) return response
                response.close()
            } catch (e: IOException) {
                if (attempt >= maxRetries) throw e
            }
            attempt++
            sleeper(backoffMillis(attempt))
        }
    }

    private fun isRetryable(code: Int) = code == 429 || code in 500..599

    private fun backoffMillis(attempt: Int): Long =
        (2.0.pow(attempt) * 1000).toLong().coerceAtMost(MAX_BACKOFF_MILLIS)

    private companion object {
        val IDEMPOTENT_METHODS = setOf("GET", "HEAD", "PUT", "DELETE", "OPTIONS")
        const val MAX_BACKOFF_MILLIS = 16_000L
    }
}
