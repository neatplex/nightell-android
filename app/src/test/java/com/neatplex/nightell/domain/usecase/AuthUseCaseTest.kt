package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.AuthRepository
import com.neatplex.nightell.utils.ITokenManager
import com.neatplex.nightell.utils.IValidation
import org.junit.Test
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.Validation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class AuthUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var tokenManager: ITokenManager
    private lateinit var authUseCase: AuthUseCase
    private lateinit var validation: IValidation

    private val user = User("","","email@example.com",1, false,"username", "password", "username")

    @Before
    fun setUp() {
        authRepository = mock(AuthRepository::class.java)
        tokenManager = mock(ITokenManager::class.java)
        validation = mock(IValidation::class.java)
        authUseCase = AuthUseCase(authRepository, tokenManager, validation)
    }

    @Test
    fun `register success`() = runTest {
        // Arrange
        val request = RegistrationRequest("username", "email@example.com", "password")
        val authResponse = AuthResponse("token", user)
        val result = Result.Success(authResponse)
        `when`(authRepository.register(request)).thenReturn(result)

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
        `when`(authRepository.register(request)).thenReturn(result)

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

        `when`(authRepository.loginWithEmail(request)).thenReturn(result)
        `when`(validation.isValidEmail(email)).thenReturn(true)

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

        `when`(authRepository.loginWithUsername(request)).thenReturn(result)
        `when`(validation.isValidEmail(username)).thenReturn(false)

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
        `when`(authRepository.signInWithGoogle(idToken)).thenReturn(result)

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
        `when`(authRepository.signInWithGoogle(idToken)).thenReturn(result)

        // Act
        val response = authUseCase.signInWithGoogle(idToken)

        // Assert
        assert(response is Result.Failure)
    }
}
