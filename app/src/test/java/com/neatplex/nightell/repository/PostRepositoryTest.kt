package com.neatplex.nightell.repository

import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.data.dto.PostCollection
import com.neatplex.nightell.data.dto.PostDetailResponse
import com.neatplex.nightell.data.dto.PostUpdateRequest
import com.neatplex.nightell.data.dto.PostUploadRequest
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import retrofit2.Response
import com.neatplex.nightell.domain.repository.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import com.neatplex.nightell.utils.Result
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class PostRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var postRepository: PostRepository

    @Before
    fun setUp() {
        apiService = Mockito.mock(ApiService::class.java)
        postRepository = PostRepository(apiService)
    }

    private val user = User("","","email@example.com",1, false,"username", "password", "username")

    private val mockPost = Post(
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

    @Test
    fun `test showFeed success`() = runBlocking {
        val postCollection = PostCollection(emptyList())
        `when`(apiService.showFeed(null)).thenReturn(Response.success(postCollection))

        val result = postRepository.showFeed(null)

        assertTrue(result is Result.Success)
        assertEquals(postCollection, (result as Result.Success).data)
    }

    @Test
    fun `test showFeed failure`() = runBlocking {
        val exception = RuntimeException("Error")
        `when`(apiService.showFeed(null)).thenThrow(exception)

        val result = postRepository.showFeed(null)

        assertTrue(result is Result.Failure)
        assertEquals("Error", (result as Result.Failure).message)
    }

    @Test
    fun `test showUserPosts success`() = runBlocking {
        val postCollection = PostCollection(emptyList())
        `when`(apiService.showUserPosts(1, null)).thenReturn(Response.success(postCollection))

        val result = postRepository.showUserPosts(1, null)

        assertTrue(result is Result.Success)
        assertEquals(postCollection, (result as Result.Success).data)
    }

    @Test
    fun `test showUserPosts failure`() = runBlocking {
        val exception = RuntimeException("Error")
        `when`(apiService.showUserPosts(1, null)).thenThrow(exception)

        val result = postRepository.showUserPosts(1, null)

        assertTrue(result is Result.Failure)
        assertEquals("Error", (result as Result.Failure).message)
    }

    @Test
    fun `test uploadPost success`() = runBlocking {
        val postDetailResponse = PostDetailResponse(mockPost)
        val request = PostUploadRequest("title", "description", 1, 1)
        `when`(apiService.uploadPost(request)).thenReturn(Response.success(postDetailResponse))

        val result = postRepository.uploadPost("title", "description", 1, 1)

        assertTrue(result is Result.Success)
        assertEquals(postDetailResponse, (result as Result.Success).data)
    }

    @Test
    fun `test uploadPost failure`() = runBlocking {
        val exception = RuntimeException("Error")
        val request = PostUploadRequest("title", "description", 1, 1)
        `when`(apiService.uploadPost(request)).thenThrow(exception)

        val result = postRepository.uploadPost("title", "description", 1, 1)

        assertTrue(result is Result.Failure)
        assertEquals("Error", (result as Result.Failure).message)
    }

    @Test
    fun `test editPost success`() = runBlocking {
        val postDetailResponse = PostDetailResponse(mockPost)
        val request = PostUpdateRequest("title", "description")
        `when`(apiService.updatePost(1, request)).thenReturn(Response.success(postDetailResponse))

        val result = postRepository.editPost("title", "description", 1)

        assertTrue(result is Result.Success)
        assertEquals(postDetailResponse, (result as Result.Success).data)
    }

    @Test
    fun `test editPost failure`() = runBlocking {
        val exception = RuntimeException("Error")
        val request = PostUpdateRequest("title", "description")
        `when`(apiService.updatePost(1, request)).thenThrow(exception)

        val result = postRepository.editPost("title", "description", 1)

        assertTrue(result is Result.Failure)
        assertEquals("Error", (result as Result.Failure).message)
    }

    @Test
    fun `test deletePost success`() = runBlocking {
        `when`(apiService.deletePost(1)).thenReturn(Response.success(Unit))

        val result = postRepository.deletePost(1)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `test deletePost failure`() = runBlocking {
        val exception = RuntimeException("Error")
        `when`(apiService.deletePost(1)).thenThrow(exception)

        val result = postRepository.deletePost(1)

        assertTrue(result is Result.Failure)
        assertEquals("Error", (result as Result.Failure).message)
    }

    @Test
    fun `test getPostById success`() = runBlocking {
        val postDetailResponse = PostDetailResponse(mockPost)
        `when`(apiService.getPostById(1)).thenReturn(Response.success(postDetailResponse))

        val result = postRepository.getPostById(1)

        assertTrue(result is Result.Success)
        assertEquals(postDetailResponse, (result as Result.Success).data)
    }

    @Test
    fun `test getPostById failure`() = runBlocking {
        val exception = RuntimeException("Error")
        `when`(apiService.getPostById(1)).thenThrow(exception)

        val result = postRepository.getPostById(1)

        assertTrue(result is Result.Failure)
        assertEquals("Error", (result as Result.Failure).message)
    }

    @Test
    fun `test search success`() = runBlocking {
        val postCollection = PostCollection(emptyList())
        `when`(apiService.searchPost("query", null)).thenReturn(Response.success(postCollection))

        val result = postRepository.search("query", null)

        assertTrue(result is Result.Success)
        assertEquals(postCollection, (result as Result.Success).data)
    }

    @Test
    fun `test search failure`() = runBlocking {
        val exception = RuntimeException("Error")
        `when`(apiService.searchPost("query", null)).thenThrow(exception)

        val result = postRepository.search("query", null)

        assertTrue(result is Result.Failure)
        assertEquals("Error", (result as Result.Failure).message)
    }

}