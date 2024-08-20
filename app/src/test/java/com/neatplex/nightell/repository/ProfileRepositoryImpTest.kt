package com.neatplex.nightell.repository

import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.UserRepository
import com.neatplex.nightell.utils.Result
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

@ExperimentalCoroutinesApi
class ProfileRepositoryImpTest {

    private lateinit var apiService : ApiService
    private lateinit var profileRepository : UserRepository

    private fun createUser(): User {
        return User("","",
            "email@example.com",
            1,
            false,
            "username",
            "password",
            "username"
        )
    }
    @Before
    fun setup() {
        apiService = mock(ApiService::class.java)
        profileRepository = UserRepository(apiService)
    }

    @Test
    fun `fetchProfile returns success when api call is successful`() = runBlocking {
        val user = createUser()
        val profile = Profile(followers_count = 100, followings_count = 200, user = user)
        val response = Response.success(profile)
        whenever(apiService.showProfile()).thenReturn(response)

        val result = profileRepository.fetchProfile()

        assertTrue(result is Result.Success)
        assertEquals(profile, (result as Result.Success).data)
    }

    @Test
    fun `fetchProfile returns failure when api call fails`() = runBlocking {
        whenever(apiService.showProfile()).thenThrow(RuntimeException("Network error"))

        val result = profileRepository.fetchProfile()

        assertTrue(result is Result.Failure)
        assertEquals("Network error", (result as Result.Failure).message)
    }

    @Test
    fun `showUserProfile returns success when api call is successful`() = runBlocking {
        val user = createUser()
        val profile = Profile(followers_count = 150, followings_count = 250, user = user)
        val response = Response.success(profile)
        whenever(apiService.showUserProfile(1)).thenReturn(response)

        val result = profileRepository.showUserProfile(1)

        assertTrue(result is Result.Success)
        assertEquals(profile, (result as Result.Success).data)
    }

    @Test
    fun `showUserProfile returns failure when api call fails`() = runBlocking {
        whenever(apiService.showUserProfile(1)).thenThrow(RuntimeException("Network error"))

        val result = profileRepository.showUserProfile(1)

        assertTrue(result is Result.Failure)
        assertEquals("Network error", (result as Result.Failure).message)
    }

    @Test
    fun `changeProfileName returns success when api call is successful`() = runBlocking {
        val user = createUser()
        val userUpdated = UserUpdated(user)
        val response = Response.success(userUpdated)

        whenever(apiService.changeProfileName(mapOf("name" to "New Name"))).thenReturn(response)

        val result = profileRepository.changeProfileName("New Name")

        assertTrue(result is Result.Success)
        assertEquals(userUpdated, (result as Result.Success).data)
    }

    @Test
    fun `changeProfileName returns failure when api call fails`() = runBlocking {
        whenever(apiService.changeProfileName(mapOf("name" to "New Name"))).thenThrow(RuntimeException("Network error"))

        val result = profileRepository.changeProfileName("New Name")

        assertTrue(result is Result.Failure)
        assertEquals("Network error", (result as Result.Failure).message)
    }

    @Test
    fun `changeProfileBio returns success when api call is successful`() = runBlocking {
        val user = createUser()
        val userUpdated = UserUpdated(user)
        val response = Response.success(userUpdated)
        whenever(apiService.changeProfileBio(mapOf("bio" to "New Bio"))).thenReturn(response)

        val result = profileRepository.changeProfileBio("New Bio")

        assertTrue(result is Result.Success)
        assertEquals(userUpdated, (result as Result.Success).data)
    }

    @Test
    fun `changeProfileBio returns failure when api call fails`() = runBlocking {
        whenever(apiService.changeProfileBio(mapOf("bio" to "New Bio"))).thenThrow(RuntimeException("Network error"))

        val result = profileRepository.changeProfileBio("New Bio")

        assertTrue(result is Result.Failure)
        assertEquals("Network error", (result as Result.Failure).message)
    }

    @Test
    fun `changeProfileUsername returns success when api call is successful`() = runBlocking {
        val user = createUser()
        val userUpdated = UserUpdated(user)
        val response = Response.success(userUpdated)
        whenever(apiService.changeProfileUsername(mapOf("username" to "new_username"))).thenReturn(response)

        val result = profileRepository.changeProfileUsername("new_username")

        assertTrue(result is Result.Success)
        assertEquals(userUpdated, (result as Result.Success).data)
    }

    @Test
    fun `changeProfileUsername returns failure when api call fails`() = runBlocking {
        whenever(apiService.changeProfileUsername(mapOf("username" to "new_username"))).thenThrow(RuntimeException("Network error"))

        val result = profileRepository.changeProfileUsername("new_username")

        assertTrue(result is Result.Failure)
        assertEquals("Network error", (result as Result.Failure).message)
    }

    @Test
    fun `deleteAccount returns success when api call is successful`() = runBlocking {
        val response = Response.success(Unit)
        whenever(apiService.deleteAccount()).thenReturn(response)

        val result = profileRepository.deleteAccount()

        assertTrue(result is Result.Success)
    }

    @Test
    fun `deleteAccount returns failure when api call fails`() = runBlocking {
        whenever(apiService.deleteAccount()).thenThrow(RuntimeException("Network error"))

        val result = profileRepository.deleteAccount()

        assertTrue(result is Result.Failure)
        assertEquals("Network error", (result as Result.Failure).message)
    }

}