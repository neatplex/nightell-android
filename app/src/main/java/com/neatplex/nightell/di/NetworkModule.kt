package com.neatplex.nightell.di

import android.content.Context
import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.data.network.AuthInterceptor
import com.neatplex.nightell.data.network.RateLimiter
import com.neatplex.nightell.data.network.RateLimiterInterceptor
import com.neatplex.nightell.data.network.RetryInterceptor
import com.neatplex.nightell.domain.repository.AuthRepository
import com.neatplex.nightell.domain.repository.AuthRepositoryImpl
import com.neatplex.nightell.domain.repository.PostRepository
import com.neatplex.nightell.domain.repository.PostRepositoryImpl
import com.neatplex.nightell.domain.repository.ProfileRepository
import com.neatplex.nightell.domain.repository.ProfileRepositoryImpl
import com.neatplex.nightell.utils.Constant
import com.neatplex.nightell.utils.ITokenManager
import com.neatplex.nightell.utils.TokenManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    abstract fun bindTokenManager(tokenManager: TokenManager): ITokenManager

    @Binds
    abstract fun bindAuthRepository(userAuthRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindPostRepository(postRepositoryImpl: PostRepositoryImpl): PostRepository

    @Binds
    abstract fun bindProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository

    companion object {
        @Provides
        @Singleton
        fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
            return TokenManager(context)
        }

        @Provides
        @Singleton
        fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
            return AuthInterceptor(tokenManager)
        }

        @Provides
        @Singleton
        fun provideRateLimiter(): RateLimiter {
            return RateLimiter(maxRequests = 5, timeWindow = 1, timeUnit = TimeUnit.MINUTES)
        }

        @Provides
        @Singleton
        fun provideRetryInterceptor(): RetryInterceptor {
            return RetryInterceptor(maxRetries = 3)
        }

        @Provides
        @Singleton
        fun provideApiService(
            authInterceptor: AuthInterceptor,
            retryInterceptor: RetryInterceptor,
            @ApplicationContext context: Context
        ): ApiService {
            val cacheSize = 10 * 1024 * 1024 // 10 MB
            val cacheDir = File(context.cacheDir, "http_cache")
            val cache = Cache(cacheDir, cacheSize.toLong())

            val okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(authInterceptor as Interceptor)
                .addInterceptor(retryInterceptor as Interceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ApiService::class.java)
        }
    }
}
