package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.AuthRepository
import com.neatplex.nightell.utils.TokenManager
import org.junit.Test
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.Validation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AuthUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var tokenManager: TokenManager
    private lateinit var authUseCase: AuthUseCase
    private lateinit var validation: Validation

    private val user = User("","","email@example.com",1, false,"username", "password", "username")

    @Before
    fun setUp() {
        authRepository = mock(AuthRepository::class.java)
        tokenManager = mock(TokenManager::class.java)
        validation = mock(Validation::class.java)
        authUseCase = AuthUseCase(authRepository, tokenManager, validation)
    }

    @Test
    fun `register success`() = runTest {
        // Arrange
        val request = RegistrationRequest("username", "email@example.com", "password")
        val authResponse = AuthResponse("token", user)
        val result = Result.Success(authResponse)
        whenever(authRepository.register(request)).thenReturn(result)

        // Act
        val response = authUseCase.register("username", "email@example.com", "password")

        // Assert
        assert(response is Result.Success)
        verify(tokenManager).setToken(authResponse.token)
        verify(tokenManager).setId(authResponse.user.id)
        verify(tokenManager).setEmail(authResponse.user.email)
    }

    @Test
    fun `register failure`() = runTest {
        // Arrange
        val request = RegistrationRequest("username", "email@example.com", "password")
        val result = Result.Failure("Error occurred", Exception())
        whenever(authRepository.register(request)).thenReturn(result)

        // Act
        val response = authUseCase.register("username", "email@example.com", "password")

        // Assert
        assert(response is Result.Failure)
    }

    @Test
    fun `login with email success`() = runTest {
        // Arrange
        val email = "email@example.com"
        val password = "password"
        val request = LoginEmailRequest(email, password)
        val authResponse = AuthResponse("token", user)
        val result = Result.Success(authResponse)

        whenever(authRepository.loginWithEmail(request)).thenReturn(result)
        whenever(validation.isValidEmail(email)).thenReturn(true)

        // Act
        val response = authUseCase.login(email, password)

        // Assert
        assert(response is Result.Success)
        verify(tokenManager).setToken(authResponse.token)
        verify(tokenManager).setId(authResponse.user.id)
        verify(tokenManager).setEmail(authResponse.user.email)
    }

    @Test
    fun `login with username success`() = runTest {
        // Arrange
        val username = "username"
        val password = "password"
        val request = LoginUsernameRequest(username, password)
        val authResponse = AuthResponse("token", user)
        val result = Result.Success(authResponse)

        whenever(authRepository.loginWithUsername(request)).thenReturn(result)
        whenever(validation.isValidEmail(username)).thenReturn(false)

        // Act
        val response = authUseCase.login(username, password)

        // Assert
        assert(response is Result.Success)
        verify(tokenManager).setToken(authResponse.token)
        verify(tokenManager).setId(authResponse.user.id)
        verify(tokenManager).setEmail(authResponse.user.email)
    }

    @Test
    fun `sign in with Google success`() = runTest {
        // Arrange
        val idToken = "google_id_token"
        val authResponse = AuthResponse("token", user)
        val result = Result.Success(authResponse)
        whenever(authRepository.signInWithGoogle(idToken)).thenReturn(result)

        // Act
        val response = authUseCase.signInWithGoogle(idToken)

        // Assert
        assert(response is Result.Success)
        verify(tokenManager).setToken(authResponse.token)
        verify(tokenManager).setId(authResponse.user.id)
        verify(tokenManager).setEmail(authResponse.user.email)
    }

    @Test
    fun `sign in with Google failure`() = runTest {
        // Arrange
        val idToken = "google_id_token"
        val result = Result.Failure("Error occurred", Exception())
        whenever(authRepository.signInWithGoogle(idToken)).thenReturn(result)

        // Act
        val response = authUseCase.signInWithGoogle(idToken)

        // Assert
        assert(response is Result.Failure)
    }
}