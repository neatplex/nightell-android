package com.neatplex.nightell.repository

import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.FollowRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import com.neatplex.nightell.utils.Result
import org.junit.Assert.assertEquals
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class FollowRepositoryTest {

    private val user = User("","","email@example.com",1, false,"username", "password", "username")
    private lateinit var apiService: ApiService
    private lateinit var followRepository: FollowRepository

    @Before
    fun setUp() {
        apiService = mock(ApiService::class.java)
        followRepository = FollowRepository(apiService)
    }

    @Test
    fun `follow should return success when api call is successful`() = runBlocking {
        // Arrange
        whenever(apiService.follow(1,2)).thenReturn(Response.success(Unit))

        // Act
        val result = followRepository.follow(1, 2)

        // Assert
        assertTrue(result is Result.Success)
    }

    @Test
    fun `follow should return failure when api call fails`() = runBlocking {
        // Arrange
        whenever(apiService.follow(1,2)).thenThrow(RuntimeException("Network error"))

        // Act
        val result = followRepository.follow(1, 2)

        // Assert
        assertTrue(result is Result.Failure)
        assertEquals("Network error", (result as Result.Failure).message)
    }

    @Test
    fun `unfollow should return success when api call is successful`() = runBlocking {
        // Arrange
        whenever(apiService.unfollow(1,2)).thenReturn(Response.success(Unit))

        // Act
        val result = followRepository.unfollow(1, 2)

        // Assert
        assertTrue(result is Result.Success)
    }

    @Test
    fun `unfollow should return failure when api call fails`() = runBlocking {
        // Arrange
        whenever(apiService.unfollow(1,2)).thenThrow(RuntimeException("Network error"))

        // Act
        val result = followRepository.unfollow(1, 2)

        // Assert
        assertTrue(result is Result.Failure)
        assertEquals("Network error", (result as Result.Failure).message)
    }

    @Test
    fun `followers should return success when api call is successful`() = runBlocking {
        // Arrange
        val users = Users(listOf(user))
        whenever(apiService.userFollowers(1)).thenReturn(Response.success(users))

        // Act
        val result = followRepository.followers(1)

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(users, (result as Result.Success).data)
    }

    @Test
    fun `followers should return failure when api call fails`() = runBlocking {
        // Arrange
        whenever(apiService.userFollowers(1)).thenThrow(RuntimeException("Network error"))

        // Act
        val result = followRepository.followers(1)

        // Assert
        assertTrue(result is Result.Failure)
        assertEquals("Network error", (result as Result.Failure).message)
    }

    @Test
    fun `followings should return success when api call is successful`() = runBlocking {
        // Arrange
        val users = Users(listOf(user))
        whenever(apiService.userFollowings(any())).thenReturn(Response.success(users))

        // Act
        val result = followRepository.followings(1)

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(users, (result as Result.Success).data)
    }

    @Test
    fun `followings should return failure when api call fails`() = runBlocking {
        // Arrange
        whenever(apiService.userFollowings(any())).thenThrow(RuntimeException("Network error"))

        // Act
        val result = followRepository.followings(1)

        // Assert
        assertTrue(result is Result.Failure)
        assertEquals("Network error", (result as Result.Failure).message)
    }

}