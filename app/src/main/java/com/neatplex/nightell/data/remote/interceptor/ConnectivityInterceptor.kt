package com.neatplex.nightell.data.remote.interceptor

import com.neatplex.nightell.domain.repository.ConnectivityMonitor
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/** Fails fast with an [IOException] when the device is offline. */
class ConnectivityInterceptor @Inject constructor(
    private val connectivityMonitor: ConnectivityMonitor,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!connectivityMonitor.isOnline.value) throw IOException("No internet connection")
        return chain.proceed(chain.request())
    }
}
