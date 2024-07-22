package com.neatplex.nightell.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.usecase.AuthUseCase
import com.neatplex.nightell.ui.auth.AuthViewModel
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
    private lateinit var authUseCase: AuthUseCase

    @Mock
    private lateinit var validation: Validation

    private lateinit var authViewModel: AuthViewModel
    private val user = User("","","email@example.com",1, false,"username", "password", "username")


    @Mock
    private lateinit var observer: Observer<Result<AuthResponse>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        authViewModel = AuthViewModel(authUseCase, validation)
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

        `when`(authUseCase.register(username, email, password)).thenReturn(Result.Success(authResponse))

        authViewModel.registerUser(username, email, password)

        verify(observer).onChanged(Result.Success(authResponse))
    }

    @Test
    fun `registerUser with invalid data should return failure`() = runTest {
        val username = "testUser"
        val email = "test@example.com"
        val password = "password123"
        val error = "Registration failed"

        `when`(authUseCase.register(username, email, password)).thenReturn(Result.Failure(error))

        authViewModel.registerUser(username, email, password)

        verify(observer).onChanged(Result.Failure(error))
    }

    @Test
    fun `loginUser with valid data should return success`() = runTest {
        val emailOrUsername = "test@example.com"
        val password = "password123"
        val authResponse = AuthResponse(token = "sampleToken", user)

        `when`(authUseCase.login(emailOrUsername, password)).thenReturn(Result.Success(authResponse))

        authViewModel.loginUser(emailOrUsername, password)

        verify(observer).onChanged(Result.Success(authResponse))
    }

    @Test
    fun `loginUser with invalid data should return failure`() = runTest {
        val emailOrUsername = "test@example.com"
        val password = "password123"
        val error = "Login failed"

        `when`(authUseCase.login(emailOrUsername, password)).thenReturn(Result.Failure(error))

        authViewModel.loginUser(emailOrUsername, password)

        verify(observer).onChanged(Result.Failure(error))
    }

    @Test
    fun `signInWithGoogle with valid data should return success`() = runTest {
        val idToken = "validToken"
        val authResponse = AuthResponse(token = "sampleToken", user)

        `when`(authUseCase.signInWithGoogle(idToken)).thenReturn(Result.Success(authResponse))

        authViewModel.signInWithGoogle(idToken)

        verify(observer).onChanged(Result.Success(authResponse))
    }

    @Test
    fun `signInWithGoogle with invalid data should return failure`() = runTest {
        val idToken = "invalidToken"
        val error = "Google sign-in failed"

        `when`(authUseCase.signInWithGoogle(idToken)).thenReturn(Result.Failure(error))

        authViewModel.signInWithGoogle(idToken)

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
