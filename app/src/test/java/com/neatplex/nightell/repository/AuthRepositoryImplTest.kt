package com.neatplex.nightell.repository

import com.neatplex.nightell.data.api.ApiService
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.AuthRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import com.neatplex.nightell.utils.Result
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import retrofit2.Response

@ExperimentalCoroutinesApi
class AuthRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var authRepository: AuthRepositoryImpl
    private val user = User("","","email@example.com",1, false,"username", "password", "username")

    @Before
    fun setUp() {
        apiService = mock(ApiService::class.java)
        authRepository = AuthRepositoryImpl(apiService)
    }

    @Test
    fun `register success`() = runTest {
        // Arrange
        val request = RegistrationRequest("username", "email@example.com", "password")
        val authResponse = AuthResponse("token", user)
        val response = Response.success(authResponse)
        whenever(apiService.register(request)).thenReturn(response)

        // Act
        val result = authRepository.register(request)

        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data == authResponse)
    }

    @Test
    fun `register failure`() = runTest {
        // Arrange
        val request = RegistrationRequest("username", "email@example.com", "password")
        val response = Response.error<AuthResponse>(400, mock(ResponseBody::class.java))
        whenever(apiService.register(request)).thenReturn(response)

        // Act
        val result = authRepository.register(request)

        // Assert
        assert(result is Result.Failure)
    }

    @Test
    fun `login with email success`() = runTest {
        // Arrange
        val request = LoginEmailRequest("email@example.com", "password")
        val authResponse = AuthResponse("token", user)
        val response = Response.success(authResponse)
        whenever(apiService.loginWithEmail(request)).thenReturn(response)

        // Act
        val result = authRepository.loginWithEmail(request)

        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data == authResponse)
    }

    @Test
    fun `login with email failure`() = runTest {
        // Arrange
        val request = LoginEmailRequest("email@example.com", "password")
        val response = Response.error<AuthResponse>(400, mock(ResponseBody::class.java))
        whenever(apiService.loginWithEmail(request)).thenReturn(response)

        // Act
        val result = authRepository.loginWithEmail(request)

        // Assert
        assert(result is Result.Failure)
    }

    @Test
    fun `login with username success`() = runTest {
        // Arrange
        val request = LoginUsernameRequest("username", "password")
        val authResponse = AuthResponse("token", user)
        val response = Response.success(authResponse)
        whenever(apiService.loginWithUsername(request)).thenReturn(response)

        // Act
        val result = authRepository.loginWithUsername(request)

        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data == authResponse)
    }

    @Test
    fun `login with username failure`() = runTest {
        // Arrange
        val request = LoginUsernameRequest("username", "password")
        val response = Response.error<AuthResponse>(400, mock(ResponseBody::class.java))
        whenever(apiService.loginWithUsername(request)).thenReturn(response)

        // Act
        val result = authRepository.loginWithUsername(request)

        // Assert
        assert(result is Result.Failure)
    }

    @Test
    fun `sign in with Google success`() = runTest {
        // Arrange
        val idToken = "google_id_token"
        val requestBody = mapOf("google_token" to idToken)
        val authResponse = AuthResponse("token", user)
        val response = Response.success(authResponse)
        whenever(apiService.signInWithGoogle(requestBody)).thenReturn(response)

        // Act
        val result = authRepository.signInWithGoogle(idToken)

        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data == authResponse)
    }

    @Test
    fun `sign in with Google failure`() = runTest {
        // Arrange
        val idToken = "google_id_token"
        val requestBody = mapOf("google_token" to idToken)
        val response = Response.error<AuthResponse>(400, mock(ResponseBody::class.java))
        whenever(apiService.signInWithGoogle(requestBody)).thenReturn(response)

        // Act
        val result = authRepository.signInWithGoogle(idToken)

        // Assert
        assert(result is Result.Failure)
    }
}
