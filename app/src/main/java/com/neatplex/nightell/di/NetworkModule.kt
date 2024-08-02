package com.neatplex.nightell.di

import android.content.Context
import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.data.network.AuthInterceptor
import com.neatplex.nightell.data.network.ConnectivityObserver
import com.neatplex.nightell.data.network.RateLimiter
import com.neatplex.nightell.data.network.RateLimiterInterceptor
import com.neatplex.nightell.data.network.RetryInterceptor
import com.neatplex.nightell.domain.repository.IAuthRepository
import com.neatplex.nightell.domain.repository.AuthRepository
import com.neatplex.nightell.domain.repository.DatabaseRepository
import com.neatplex.nightell.domain.repository.FileRepository
import com.neatplex.nightell.domain.repository.FollowRepository
import com.neatplex.nightell.domain.repository.IDatabaseRepository
import com.neatplex.nightell.domain.repository.IFileRepository
import com.neatplex.nightell.domain.repository.IFollowRepository
import com.neatplex.nightell.domain.repository.ILikeRepository
import com.neatplex.nightell.domain.repository.IPostRepository
import com.neatplex.nightell.domain.repository.PostRepository
import com.neatplex.nightell.domain.repository.IProfileRepository
import com.neatplex.nightell.domain.repository.LikeRepository
import com.neatplex.nightell.domain.repository.ProfileRepository
import com.neatplex.nightell.utils.Constant
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
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {


    @Binds
    abstract fun bindAuthRepository(userAuthRepository: AuthRepository): IAuthRepository

    @Binds
    abstract fun bindLikeRepository(likeRepository: LikeRepository): ILikeRepository

    @Binds
    abstract fun bindPostRepository(postRepository: PostRepository): IPostRepository

    @Binds
    abstract fun bindProfileRepository(profileRepositoryImpl: ProfileRepository): IProfileRepository

    @Binds
    abstract fun bindFileRepository(fileRepository: FileRepository): IFileRepository

    @Binds
    abstract fun bindFollowRepository(followRepository: FollowRepository): IFollowRepository

    @Binds
    abstract fun bindDatabaseRepository(databaseRepository: DatabaseRepository): IDatabaseRepository

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
            return RateLimiter(maxRequests = 8, timeWindow = 2, timeUnit = TimeUnit.MINUTES)
        }

        @Provides
        @Singleton
        fun provideRetryInterceptor(): RetryInterceptor {
            return RetryInterceptor(maxRetries = 3)
        }

        @Provides
        @Singleton
        fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {
            return ConnectivityObserver(context)
        }

        @Provides
        @Singleton
        fun provideApiService(
            authInterceptor: AuthInterceptor,
            retryInterceptor: RetryInterceptor,
            rateLimiter: RateLimiter,
            connectivityObserver: ConnectivityObserver,
            @ApplicationContext context: Context
        ): ApiService {
            val cacheSize = 10 * 1024 * 1024 // 10 MB
            val cacheDir = File(context.cacheDir, "http_cache")
            val cache = Cache(cacheDir, cacheSize.toLong())

            val okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(authInterceptor as Interceptor)
                .addInterceptor(retryInterceptor as Interceptor)
                .addInterceptor(RateLimiterInterceptor(rateLimiter))
                .addInterceptor { chain ->
                if (!connectivityObserver.isConnected.value) {
                    throw IOException("No internet connection")
                }
                chain.proceed(chain.request())
            }
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
