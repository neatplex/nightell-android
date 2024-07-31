package com.neatplex.nightell.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.AuthRepository
import com.neatplex.nightell.domain.usecase.AuthUseCase
import com.neatplex.nightell.ui.auth.AuthViewModel
import com.neatplex.nightell.utils.ITokenManager
import com.neatplex.nightell.utils.IValidation
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.Validation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var tokenManager: ITokenManager

    @Mock
    private lateinit var validation: IValidation

    private lateinit var authUseCase: AuthUseCase
    private lateinit var authViewModel: AuthViewModel

    private val user = User("", "", "email@example.com", 1, false, "username", "password", "username")

    @Mock
    private lateinit var observer: Observer<Result<AuthResponse>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Initialize AuthUseCase with mocked dependencies
        authUseCase = AuthUseCase(authRepository, tokenManager, validation)

        // Initialize AuthViewModel with the real AuthUseCase
        authViewModel = AuthViewModel(authUseCase, validation)

        // Observe the LiveData
        authViewModel.authResult.observeForever(observer)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        authViewModel.authResult.removeObserver(observer)
    }

    @Test
    fun `registerUser with valid data should return success`() = runTest {
        val username = "testUser"
        val email = "test@example.com"
        val password = "password123"
        val authResponse = AuthResponse(token = "sampleToken", user)

        // Mock the repository and validation behavior
        val request = RegistrationRequest(username, email, password)
        `when`(authRepository.register(request)).thenReturn(Result.Success(authResponse))
        `when`(validation.isValidEmail(email)).thenReturn(true)
        `when`(validation.isValidPassword(password)).thenReturn(true)
        `when`(validation.isValidUsername(username)).thenReturn(true)

        // Perform the action
        authViewModel.registerUser(username, email, password)

        // Verify the observer received the expected result
        verify(observer).onChanged(Result.Success(authResponse))
    }

    @Test
    fun `registerUser with invalid data should return failure`() = runTest {
        val username = "testUser"
        val email = "invalidEmail"
        val password = "password123"
        val error = "Invalid email"

        // Mock the validation behavior
        `when`(validation.isValidEmail(email)).thenReturn(false)

        // Mock the repository to return failure if invalid data
        val request = RegistrationRequest(username, email, password)
        `when`(authRepository.register(request)).thenReturn(Result.Failure(error))

        // Perform the action
        authViewModel.registerUser(username, email, password)

        // Verify the observer received the expected failure result
        verify(observer).onChanged(Result.Failure(error))
    }

    @Test
    fun `loginUser with valid data should return success`() = runTest {
        val emailOrUsername = "test@example.com"
        val password = "password123"
        val authResponse = AuthResponse(token = "sampleToken", user)

        // Mock the validation and repository behavior
        `when`(validation.isValidEmail(emailOrUsername)).thenReturn(true)
        val request = LoginEmailRequest(emailOrUsername, password)
        `when`(authRepository.loginWithEmail(request)).thenReturn(Result.Success(authResponse))

        // Perform the action
        authViewModel.loginUser(emailOrUsername, password)

        // Verify the observer received the expected result
        verify(observer).onChanged(Result.Success(authResponse))
    }

    @Test
    fun `loginUser with invalid data should return failure`() = runTest {
        val emailOrUsername = "test@example.com"
        val password = "wrongPassword"
        val error = "Login failed"

        // Mock the validation and repository behavior
        `when`(validation.isValidEmail(emailOrUsername)).thenReturn(true)
        val request = LoginEmailRequest(emailOrUsername, password)
        `when`(authRepository.loginWithEmail(request)).thenReturn(Result.Failure(error))

        // Perform the action
        authViewModel.loginUser(emailOrUsername, password)

        // Verify the observer received the expected failure result
        verify(observer).onChanged(Result.Failure(error))
    }

    @Test
    fun `signInWithGoogle with valid data should return success`() = runTest {
        val idToken = "validToken"
        val authResponse = AuthResponse(token = "sampleToken", user)

        // Mock the repository behavior
        `when`(authRepository.signInWithGoogle(idToken)).thenReturn(Result.Success(authResponse))

        // Perform the action
        authViewModel.signInWithGoogle(idToken)

        // Verify the observer received the expected result
        verify(observer).onChanged(Result.Success(authResponse))
    }

    @Test
    fun `signInWithGoogle with invalid data should return failure`() = runTest {
        val idToken = "invalidToken"
        val error = "Google sign-in failed"

        // Mock the repository behavior
        `when`(authRepository.signInWithGoogle(idToken)).thenReturn(Result.Failure(error))

        // Perform the action
        authViewModel.signInWithGoogle(idToken)

        // Verify the observer received the expected failure result
        verify(observer).onChanged(Result.Failure(error))
    }

    @Test
    fun `isValidEmail should return true for valid email`() {
        val email = "test@example.com"
        `when`(validation.isValidEmail(email)).thenReturn(true)

        val result = authViewModel.isValidEmail(email)

        Assert.assertTrue(result)
    }

    @Test
    fun `isValidEmail should return false for invalid email`() {
        val email = "invalidEmail"
        `when`(validation.isValidEmail(email)).thenReturn(false)

        val result = authViewModel.isValidEmail(email)

        Assert.assertFalse(result)
    }

    @Test
    fun `isValidPassword should return true for valid password`() {
        val password = "password123"
        `when`(validation.isValidPassword(password)).thenReturn(true)

        val result = authViewModel.isValidPassword(password)

        Assert.assertTrue(result)
    }

    @Test
    fun `isValidPassword should return false for invalid password`() {
        val password = "short"
        `when`(validation.isValidPassword(password)).thenReturn(false)

        val result = authViewModel.isValidPassword(password)

        Assert.assertFalse(result)
    }

    @Test
    fun `isValidUsername should return true for valid username`() {
        val username = "validUsername"
        `when`(validation.isValidUsername(username)).thenReturn(true)

        val result = authViewModel.isValidUsername(username)

        Assert.assertTrue(result)
    }

    @Test
    fun `isValidUsername should return false for invalid username`() {
        val username = "inv"
        `when`(validation.isValidUsername(username)).thenReturn(false)

        val result = authViewModel.isValidUsername(username)

        Assert.assertFalse(result)
    }
}
