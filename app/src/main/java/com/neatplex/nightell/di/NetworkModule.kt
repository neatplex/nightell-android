package com.neatplex.nightell.di

import android.content.Context
import com.neatplex.nightell.data.api.ApiService
import com.neatplex.nightell.domain.repository.PostRepository
import com.neatplex.nightell.domain.repository.PostRepositoryImpl
import com.neatplex.nightell.domain.repository.AuthRepository
import com.neatplex.nightell.domain.repository.AuthRepositoryImpl
import com.neatplex.nightell.utils.Constant
import com.neatplex.nightell.utils.TokenManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    abstract fun bindUserAuthRepository(
        userAuthRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindPostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ) : PostRepository

    companion object {
        @Provides
        @Singleton
        fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
            return TokenManager(context)
        }

        @Provides
        @Singleton
        fun provideApiService(authInterceptor: AuthInterceptor): ApiService {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build().create(ApiService::class.java)
        }

        @Provides
        @Singleton
        fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
            return AuthInterceptor(tokenManager)

        }
    }

}

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = tokenManager.getToken()

        // Add the Authorization header if the request requires it
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

        return chain.proceed(newRequest)
    }

    private fun requiresAuthorization(request: Request): Boolean {
        // Implement your logic to determine if the request requires Authorization header
        // For example, check the URL or method
        val requiresAuthorization = when {
            request.url.encodedPath.contains("sign-up") ||
                    request.url.encodedPath.contains("sign-in") -> false

            else -> true
        }
        return requiresAuthorization
    }

}