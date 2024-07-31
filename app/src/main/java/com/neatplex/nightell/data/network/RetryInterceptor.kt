package com.neatplex.nightell.data.network

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
                response?.close()
                response = chain.proceed(request)
            } catch (e: IOException) {
                exception = e
                Log.e("RetryInterceptor", "IOException: retrying... ($tryCount/$maxRetries)", e)
            }

            tryCount++
            if (response == null || !response.isSuccessful) {
                val backoff = (2.0.pow(tryCount) * 1000).toLong().coerceAtMost(16000) // Cap backoff time
                Log.d("RetryInterceptor", "Retry $tryCount, backoff $backoff ms")
                runBlocking {
                    delay(backoff)
                }
            }
        }

        if (response == null && exception != null) {
            throw exception
        }

        return response!!
    }
}
