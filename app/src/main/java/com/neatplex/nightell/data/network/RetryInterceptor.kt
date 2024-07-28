package com.neatplex.nightell.data.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.math.pow

class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var response: Response? = null
        var exception: IOException? = null
        var tryCount = 0

        while (tryCount < maxRetries && (response == null || !response.isSuccessful)) {
            try {
                response = chain.proceed(request)
            } catch (e: IOException) {
                exception = e
            }

            tryCount++
            if (response == null || !response.isSuccessful) {
                Thread.sleep((2.0.pow(tryCount) * 1000).toLong()) // Exponential backoff
            }
        }

        if (response == null && exception != null) {
            throw exception
        }

        return response!!
    }
}