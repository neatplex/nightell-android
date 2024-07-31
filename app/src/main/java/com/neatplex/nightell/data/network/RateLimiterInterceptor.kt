package com.neatplex.nightell.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RateLimiterInterceptor @Inject constructor(private val rateLimiter: RateLimiter) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return rateLimiter.acquire()
            .let {
                Log.d("RateLimiterInterceptor", "Request acquired")
                chain.proceed(chain.request())
            }
    }
}