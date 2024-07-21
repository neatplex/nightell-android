package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.ProfileRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import com.neatplex.nightell.utils.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class ProfileUseCaseTest {

    private lateinit var profileRepository: ProfileRepository
    private lateinit var profileUseCase: ProfileUseCase

    @Before
    fun setUp() {
        profileRepository = mock(ProfileRepository::class.java)
        profileUseCase = ProfileUseCase(profileRepository)
    }

    private fun createUser(): User {
        return User(
            bio = "Sample Bio",
            created_at = "2023-07-18",
            email = "user@example.com",
            id = 1,
            is_banned = false,
            name = "John Doe",
            password = "password",
            username = "john_doe"
        )
    }

    @Test
    fun `profile returns success when repository call is successful`() = runBlocking {
        val user = createUser()
        val profile = Profile(followers_count = 100, followings_count = 200, user = user)
        val result = Result.Success(profile)
        whenever(profileRepository.fetchProfile()).thenReturn(result)

        val useCaseResult = profileUseCase.profile()

        assertTrue(useCaseResult is Result.Success)
        assertEquals(profile, (useCaseResult as Result.Success).data)
    }

    @Test
    fun `profile returns failure when repository call fails`() = runBlocking {
        val result = Result.Failure("Network error")
        whenever(profileRepository.fetchProfile()).thenReturn(result)

        val useCaseResult = profileUseCase.profile()

        assertTrue(useCaseResult is Result.Failure)
        assertEquals("Network error", (useCaseResult as Result.Failure).message)
    }

    @Test
    fun `showUserProfile returns success when repository call is successful`() = runBlocking {
        val user = createUser()
        val profile = Profile(followers_count = 150, followings_count = 250, user = user)
        val result = Result.Success(profile)
        whenever(profileRepository.showUserProfile(1)).thenReturn(result)

        val useCaseResult = profileUseCase.getUserProfile(1)

        assertTrue(useCaseResult is Result.Success)
        assertEquals(profile, (useCaseResult as Result.Success).data)
    }

    @Test
    fun `showUserProfile returns failure when repository call fails`() = runBlocking {
        val result = Result.Failure("Network error")
        whenever(profileRepository.showUserProfile(1)).thenReturn(result)

        val useCaseResult = profileUseCase.getUserProfile(1)

        assertTrue(useCaseResult is Result.Failure)
        assertEquals("Network error", (useCaseResult as Result.Failure).message)
    }

    @Test
    fun `changeProfileName returns success when repository call is successful`() = runBlocking {
        val user = createUser()
        val userUpdated = UserUpdated(user)
        val result = Result.Success(userUpdated)
        whenever(profileRepository.changeProfileName("New Name")).thenReturn(result)

        val useCaseResult = profileUseCase.changeProfileName("New Name")

        assertTrue(useCaseResult is Result.Success)
        assertEquals(userUpdated, (useCaseResult as Result.Success).data)
    }

    @Test
    fun `changeProfileName returns failure when repository call fails`() = runBlocking {
        val result = Result.Failure("Network error")
        whenever(profileRepository.changeProfileName("New Name")).thenReturn(result)

        val useCaseResult = profileUseCase.changeProfileName("New Name")

        assertTrue(useCaseResult is Result.Failure)
        assertEquals("Network error", (useCaseResult as Result.Failure).message)
    }

    @Test
    fun `changeProfileBio returns success when repository call is successful`() = runBlocking {
        val user = createUser()
        val userUpdated = UserUpdated(user)
        val result = Result.Success(userUpdated)
        whenever(profileRepository.changeProfileBio("New Bio")).thenReturn(result)

        val useCaseResult = profileUseCase.changeProfileBio("New Bio")

        assertTrue(useCaseResult is Result.Success)
        assertEquals(userUpdated, (useCaseResult as Result.Success).data)
    }

    @Test
    fun `changeProfileBio returns failure when repository call fails`() = runBlocking {
        val result = Result.Failure("Network error")
        whenever(profileRepository.changeProfileBio("New Bio")).thenReturn(result)

        val useCaseResult = profileUseCase.changeProfileBio("New Bio")

        assertTrue(useCaseResult is Result.Failure)
        assertEquals("Network error", (useCaseResult as Result.Failure).message)
    }

    @Test
    fun `changeProfileUsername returns success when repository call is successful`() = runBlocking {
        val user = createUser()
        val userUpdated = UserUpdated(user)
        val result = Result.Success(userUpdated)
        whenever(profileRepository.changeProfileUsername("new_username")).thenReturn(result)

        val useCaseResult = profileUseCase.changeProfileUsername("new_username")

        assertTrue(useCaseResult is Result.Success)
        assertEquals(userUpdated, (useCaseResult as Result.Success).data)
    }

    @Test
    fun `changeProfileUsername returns failure when repository call fails`() = runBlocking {
        val result = Result.Failure("Network error")
        whenever(profileRepository.changeProfileUsername("new_username")).thenReturn(result)

        val useCaseResult = profileUseCase.changeProfileUsername("new_username")

        assertTrue(useCaseResult is Result.Failure)
        assertEquals("Network error", (useCaseResult as Result.Failure).message)
    }

    @Test
    fun `deleteAccount returns success when repository call is successful`() = runBlocking {
        val result = Result.Success(Unit)
        whenever(profileRepository.deleteAccount()).thenReturn(result)

        val useCaseResult = profileUseCase.deleteAccount()

        assertTrue(useCaseResult is Result.Success)
    }

    @Test
    fun `deleteAccount returns failure when repository call fails`() = runBlocking {
        val result = Result.Failure("Network error")
        whenever(profileRepository.deleteAccount()).thenReturn(result)

        val useCaseResult = profileUseCase.deleteAccount()

        assertTrue(useCaseResult is Result.Failure)
        assertEquals("Network error", (useCaseResult as Result.Failure).message)
    }
}