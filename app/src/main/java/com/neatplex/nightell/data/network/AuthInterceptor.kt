package com.neatplex.nightell.data.network

import com.neatplex.nightell.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .apply {
                    if (requiresAuthorization(originalRequest)) {
                        header("Authorization", "Bearer $token")
                    }
                }
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(newRequest)

        if (response.code == 401 && !newRequest.url.encodedPath.contains("auth")
        ) {
            tokenManager.logoutForce()
        }

        return response
    }

    private fun requiresAuthorization(request: Request): Boolean {
        return !request.url.encodedPath.contains("sign")
    }
}
