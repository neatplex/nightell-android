package com.neatplex.nightell.di

import android.content.Context
import com.neatplex.nightell.data.remote.ApiConfig
import com.neatplex.nightell.data.remote.api.ApiService
import com.neatplex.nightell.data.remote.interceptor.AuthInterceptor
import com.neatplex.nightell.data.remote.interceptor.ConnectivityInterceptor
import com.neatplex.nightell.data.remote.interceptor.RetryInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val CACHE_SIZE_BYTES = 10L * 1024 * 1024
    private const val TIMEOUT_SECONDS = 10L

    /** Up to 2 retries, i.e. 3 attempts in total, as before. */
    private const val MAX_RETRIES = 2

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        authInterceptor: AuthInterceptor,
        connectivityInterceptor: ConnectivityInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(File(context.cacheDir, "http_cache"), CACHE_SIZE_BYTES))
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(connectivityInterceptor)
        .addInterceptor(authInterceptor)
        .addInterceptor(RetryInterceptor(MAX_RETRIES))
        .build()

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(ApiService::class.java)
}
