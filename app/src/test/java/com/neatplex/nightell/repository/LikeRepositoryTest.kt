package com.neatplex.nightell.repository

import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.data.dto.Likes
import com.neatplex.nightell.data.dto.StoreLike
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.model.Like
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.LikeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.neatplex.nightell.utils.Result
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

@ExperimentalCoroutinesApi
class LikeRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var likeRepository: LikeRepository
    private val user = User("","","email@example.com",1, false,"username", "password", "username")
    private val post = Post(
        audio = CustomFile("MP3",1,"audio_url",1),
        audio_id = 1,
        comments_count = 10,
        created_at = "2023-01-01T00:00:00Z",
        description = "Description",
        id = 1,
        image = CustomFile("JPG",1,"image_url",1),
        image_id = 1,
        likes = listOf(),
        likes_count = 5,
        title = "Title",
        user = user,
        user_id = 1
    )
    private val like = Like(1,user,"01-01-01",1,1,post)


    @Before
    fun setup(){
        apiService = mock(ApiService::class.java)
        likeRepository = LikeRepository(apiService)
    }

    @Test
    fun `like post success`() = runTest {

        // Arrange
        whenever(apiService.like(1)).thenReturn(Response.success(StoreLike(like)))

        // Act
        val result = likeRepository.like(1)

        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data == StoreLike(like))
    }

    @Test
    fun `like post failure`() = runTest {

        // Arrange
        whenever(apiService.like(1)).thenThrow(RuntimeException("Network error"))

        // Act
        val result = likeRepository.like(1)

        // Assert
        assert(result is Result.Failure)
        assert((result as Result.Failure).message.equals("Network error"))
    }

    @Test
    fun `show likes success`() = runTest {
        val likes = Likes(listOf(like))
        // Arrange
        whenever(apiService.getLikes(1)).thenReturn(Response.success(likes))

        // Act
        val result = likeRepository.showLikes(1)

        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data == likes)
    }

    @Test
    fun `show likes failure`() = runTest {

        // Arrange
        whenever(apiService.getLikes(1)).thenThrow(RuntimeException("Network error"))

        // Act
        val result = likeRepository.showLikes(1)

        // Assert
        assert(result is Result.Failure)
        assert((result as Result.Failure).message.equals("Network error"))
    }

    @Test
    fun `delete like success`() = runTest {

        // Arrange
        whenever(apiService.deleteLike(1)).thenReturn(Response.success(Unit))

        // Act
        val result = likeRepository.deleteLike(1)

        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data == Unit)
    }

    @Test
    fun `delete like failure`() = runTest {

        // Arrange
        whenever(apiService.deleteLike(1)).thenThrow(RuntimeException("Network error"))

        // Act
        val result = likeRepository.deleteLike(1)

        // Assert
        assert(result is Result.Failure)
        assert((result as Result.Failure).message.equals("Network error"))
    }
}