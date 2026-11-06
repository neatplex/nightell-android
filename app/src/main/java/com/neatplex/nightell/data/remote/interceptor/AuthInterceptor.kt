package com.neatplex.nightell.data.remote.interceptor

import com.neatplex.nightell.domain.repository.SessionRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

/**
 * Adds the bearer token to authenticated requests, and expires the session
 * when the server answers 401 to one of them.
 */
class AuthInterceptor @Inject constructor(
    private val sessionRepository: SessionRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = sessionRepository.token.value

        val authorizedRequest = if (token != null && requiresAuthorization(request)) {
            request.newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            request
        }

        val response = chain.proceed(authorizedRequest)
        if (response.code == 401 && !isAuthEndpoint(request)) {
            sessionRepository.expireSession()
        }
        return response
    }

    private fun requiresAuthorization(request: Request) = !request.url.encodedPath.contains("sign")

    private fun isAuthEndpoint(request: Request) = request.url.encodedPath.contains("auth")
}
