package com.neatplex.nightell.data.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RateLimiterInterceptor @Inject constructor(private val rateLimiter: RateLimiter) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        rateLimiter.acquire()
        return chain.proceed(chain.request())
    }
}