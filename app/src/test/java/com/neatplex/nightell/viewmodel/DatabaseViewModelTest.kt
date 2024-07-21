package com.neatplex.nightell.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.neatplex.nightell.domain.model.PostEntity
import com.neatplex.nightell.domain.repository.DatabaseRepository
import com.neatplex.nightell.ui.viewmodel.DatabaseViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DatabaseViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val repository: DatabaseRepository = mockk()

    private lateinit var viewModel: DatabaseViewModel

    private val mockPost = PostEntity(1, "Post 1", "user" , "path")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = DatabaseViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `init should fetch all posts and update savedPosts`() = runTest {
        // Arrange
        val mockPosts = listOf(mockPost)
        every { repository.getAllPosts() } returns flowOf(mockPosts)

        // Act
        viewModel = DatabaseViewModel(repository)

        // Assert
        assertEquals(mockPosts, viewModel.savedPosts.value)
    }

    @Test
    fun `savePost should insert post and update savedPosts`() = runTest {
        // Arrange
        coEvery { repository.insertPost(mockPost) } just Runs
        coEvery { repository.getAllPosts() } returns flowOf(listOf(mockPost))

        // Act
        viewModel.savePost(mockPost)

        // Assert
        coVerify { repository.insertPost(mockPost) }
    }

    @Test
    fun `unsavePost should delete post and update savedPosts`() = runTest {
        // Arrange
        coEvery { repository.deletePost(mockPost) } just Runs
        coEvery { repository.getAllPosts() } returns flowOf(emptyList())

        // Act
        viewModel.unsavePost(mockPost)

        // Assert
        coVerify { repository.deletePost(mockPost) }
        assertEquals(emptyList<PostEntity>(), viewModel.savedPosts.value)
    }

    @Test
    fun `getPostById should return the correct post`() = runTest {
        // Arrange
        coEvery { repository.getPostById(1) } returns mockPost
        var result: PostEntity? = null

        // Act
        viewModel.getPostById(1) { postEntity ->
            result = postEntity
        }

        // Assert
        coVerify { repository.getPostById(1) }
        assertEquals(mockPost, result)
    }
}
