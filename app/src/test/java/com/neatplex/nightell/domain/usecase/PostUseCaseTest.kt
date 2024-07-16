package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.PostCollection
import com.neatplex.nightell.data.dto.PostDetailResponse
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import com.neatplex.nightell.utils.Result
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class PostUseCaseTest {

    private lateinit var postRepository: PostRepository
    private lateinit var postUseCase: PostUseCase

    @Before
    fun setup() {
        postRepository = mock(PostRepository::class.java)
        postUseCase = PostUseCase(postRepository)
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
    fun `test loadFeed success`() = runBlocking {
        val postCollection = PostCollection(listOf(mockPost))
        `when`(postRepository.showFeed(null)).thenReturn(Result.Success(postCollection))

        val result = postUseCase.loadFeed(null)

        assertTrue(result is Result.Success)
        assertEquals(listOf(mockPost), (result as Result.Success).data)
    }

    @Test
    fun `test loadFeed failure`() = runBlocking {
        `when`(postRepository.showFeed(null)).thenReturn(Result.Failure("Error", null))

        val result = postUseCase.loadFeed(null)

        assertTrue(result is Result.Failure)
        assertEquals("Error loading feed", (result as Result.Failure).message)
    }

    @Test
    fun `test loadUserPosts success`() = runBlocking {
        val postCollection = PostCollection(listOf(mockPost))
        `when`(postRepository.showUserPosts(1, null)).thenReturn(Result.Success(postCollection))

        val result = postUseCase.loadUserPosts(1, null)

        assertTrue(result is Result.Success)
        assertEquals(listOf(mockPost), (result as Result.Success).data)
    }

    @Test
    fun `test loadUserPosts failure`() = runBlocking {
        `when`(postRepository.showUserPosts(1, null)).thenReturn(Result.Failure("Error", null))

        val result = postUseCase.loadUserPosts(1, null)

        assertTrue(result is Result.Failure)
        assertEquals("Error loading feed", (result as Result.Failure).message)
    }

    @Test
    fun `test uploadPost success`() = runBlocking {
        val postDetailResponse = PostDetailResponse(mockPost)
        `when`(postRepository.uploadPost("title", "description", 1, 1)).thenReturn(Result.Success(postDetailResponse))

        val result = postUseCase.uploadPost("title", "description", 1, 1)

        assertTrue(result is Result.Success)
        assertEquals(postDetailResponse, (result as Result.Success).data)
    }

    @Test
    fun `test uploadPost failure`() = runBlocking {
        `when`(postRepository.uploadPost("title", "description", 1, 1)).thenReturn(Result.Failure("Error", null))

        val result = postUseCase.uploadPost("title", "description", 1, 1)

        assertTrue(result is Result.Failure)
        assertEquals("Error", (result as Result.Failure).message)
    }

    @Test
    fun `test editPost success`() = runBlocking {
        val postDetailResponse = PostDetailResponse(mockPost)
        `when`(postRepository.editPost("title", "description", 1)).thenReturn(Result.Success(postDetailResponse))

        val result = postUseCase.editPost(1, "title", "description")

        assertTrue(result is Result.Success)
        assertEquals(postDetailResponse, (result as Result.Success).data)
    }

    @Test
    fun `test editPost failure`() = runBlocking {
        `when`(postRepository.editPost("title", "description", 1)).thenReturn(Result.Failure("Error", null))

        val result = postUseCase.editPost(1, "title", "description")

        assertTrue(result is Result.Failure)
        assertEquals("Error", (result as Result.Failure).message)
    }

    @Test
    fun `test deletePost success`() = runBlocking {
        `when`(postRepository.deletePost(1)).thenReturn(Result.Success(Unit))

        val result = postUseCase.deletePost(1)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `test deletePost failure`() = runBlocking {
        `when`(postRepository.deletePost(1)).thenReturn(Result.Failure("Error", null))

        val result = postUseCase.deletePost(1)

        assertTrue(result is Result.Failure)
        assertEquals("Error", (result as Result.Failure).message)
    }

    @Test
    fun `test search success`() = runBlocking {
        val postCollection = PostCollection(listOf(mockPost))
        `when`(postRepository.search("query", null)).thenReturn(Result.Success(postCollection))

        val result = postUseCase.search("query", null)

        assertTrue(result is Result.Success)
        assertEquals(listOf(mockPost), (result as Result.Success).data)
    }

    @Test
    fun `test search failure`() = runBlocking {
        `when`(postRepository.search("query", null)).thenReturn(Result.Failure("Error", null))

        val result = postUseCase.search("query", null)

        assertTrue(result is Result.Failure)
        assertEquals("Error loading searched posts", (result as Result.Failure).message)
    }

    @Test
    fun `test getPostDetail success`() = runBlocking {
        val postDetailResponse = PostDetailResponse(mockPost)
        `when`(postRepository.getPostById(1)).thenReturn(Result.Success(postDetailResponse))

        val result = postUseCase.getPostDetail(1)

        assertTrue(result is Result.Success)
        assertEquals(postDetailResponse, (result as Result.Success).data)
    }

    @Test
    fun `test getPostDetail failure`() = runBlocking {
        `when`(postRepository.getPostById(1)).thenReturn(Result.Failure("Error", null))

        val result = postUseCase.getPostDetail(1)

        assertTrue(result is Result.Failure)
        assertEquals("Error", (result as Result.Failure).message)
    }
}