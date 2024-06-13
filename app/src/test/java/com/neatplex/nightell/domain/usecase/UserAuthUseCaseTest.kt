package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.AuthRepositoryImpl
import com.neatplex.nightell.utils.TokenManager
import org.junit.Test
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.Validation
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class UserAuthUseCaseTest {

    @Mock
    lateinit var userAuthRepositoryImpl: AuthRepositoryImpl

    @Mock
    lateinit var tokenManager: TokenManager

    @InjectMocks
    lateinit var userAuthUseCase: UserAuthUseCase

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        testDispatcher.cleanupTestCoroutines()
    }

    private val user = User(
        bio = "bio",
        created_at = "2021-01-01",
        email = "test@example.com",
        id = 1,
        is_banned = false,
        name = "Test User",
        password = "password",
        username = "testuser"
    )

    private val authResponse = AuthResponse(
        token = "token",
        user = user
    )

    @Test
    fun `register success`() = runBlockingTest {
        val request = RegistrationRequest("testuser", "test@example.com", "password")
        val result = Result.Success(authResponse)

        whenever(userAuthRepositoryImpl.register(request)).thenReturn(result)

        val actualResult = userAuthUseCase.register("testuser", "test@example.com", "password")

        assertEquals(result, actualResult)
        verify(tokenManager).setToken(authResponse.token)
        verify(tokenManager).setId(authResponse.user.id)
        verify(tokenManager).setEmail(authResponse.user.email)
    }

    @Test
    fun `login with email success`() = runBlockingTest {
        // Mock Validation behavior
        val validEmail = "test@example.com"
        mockkObject(Validation)
        every { Validation.isValidEmail(validEmail) } returns true

        val request = LoginEmailRequest(validEmail, "password")
        val result = Result.Success(authResponse)

        `when`(userAuthRepositoryImpl.loginWithEmail(request)).thenReturn(result)

        val actualResult = userAuthUseCase.login(validEmail, "password")

        assertEquals(result, actualResult)
        verify(tokenManager).setToken(authResponse.token)
        verify(tokenManager).setId(authResponse.user.id)
        verify(tokenManager).setEmail(authResponse.user.email)
    }

    @Test
    fun `login with username success`() = runBlockingTest {
        // Mock Validation behavior
        val username = "example"
        mockkObject(Validation)
        every { Validation.isValidEmail(username) } returns false

        val request = LoginUsernameRequest(username, "password")
        val result = Result.Success(authResponse)

        `when`(userAuthRepositoryImpl.loginWithUsername(request)).thenReturn(result)

        val actualResult = userAuthUseCase.login(username, "password")

        assertEquals(result, actualResult)
        verify(tokenManager).setToken(authResponse.token)
        verify(tokenManager).setId(authResponse.user.id)
        verify(tokenManager).setEmail(authResponse.user.email)
    }

    @Test
    fun `register failure`() = runBlockingTest {
        val request = RegistrationRequest("testuser", "test@example.com", "password")
        val result = Result.Error("This username is already reserved.")

        // Mock the repository response
        `when`(userAuthRepositoryImpl.register(request)).thenReturn(result)

        val actualResult = userAuthUseCase.register("testuser", "test@example.com", "password")

        // Assert that the result is as expected
        assertEquals(result, actualResult)

        // Verify that tokenManager methods are never called
        verify(tokenManager, never()).setToken(anyString())
        verify(tokenManager, never()).setId(anyInt())
        verify(tokenManager, never()).setEmail(anyString())
    }

    @Test
    fun `login failure`() = runBlockingTest {
        // Mock Validation behavior
        val validEmail = "test@example.com"
        mockkObject(Validation)
        every { Validation.isValidEmail(validEmail) } returns true

        val request = LoginEmailRequest(validEmail, "password")
        val result = Result.Error("Email or password is incorrect.")

        `when`(userAuthRepositoryImpl.loginWithEmail(request)).thenReturn(result)

        val actualResult = userAuthUseCase.login(validEmail, "password")

        assertEquals(result, actualResult)
        verify(tokenManager, never()).setToken(anyString())
        verify(tokenManager, never()).setId(anyInt())
        verify(tokenManager, never()).setEmail(anyString())
    }
}