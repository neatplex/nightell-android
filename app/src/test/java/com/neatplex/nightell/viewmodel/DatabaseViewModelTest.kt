package com.neatplex.nightell.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.neatplex.nightell.domain.model.PostEntity
import com.neatplex.nightell.domain.repository.DatabaseRepository
import com.neatplex.nightell.ui.viewmodel.DatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class DatabaseViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DatabaseViewModel
    private val repository: DatabaseRepository = mock()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val post = PostEntity(1, "Title", "Content","path")
    private val postsFlow = MutableStateFlow<List<PostEntity>>(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(repository.getAllPosts()).thenReturn(postsFlow)
        viewModel = DatabaseViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test getAllPosts called on initialization`() = testScope.runTest {
        verify(repository).getAllPosts()
    }

    @Test
    fun `test savePost calls insertPost`() = testScope.runTest {
        viewModel.savePost(post)

        verify(repository).insertPost(post)
    }

    @Test
    fun `test unsavePost calls deletePost and updates savedPosts`() = testScope.runTest {
        // Initial state with one post
        postsFlow.value = listOf(post)
        doAnswer {
            postsFlow.value = emptyList()
        }.whenever(repository).deletePost(post)

        viewModel.unsavePost(post)

        verify(repository).deletePost(post)

        // Advance the dispatcher until all work is complete
        advanceUntilIdle()

        // Verify the state flow is updated correctly
        assertThat(viewModel.savedPosts.first()).isEmpty()
    }

    @Test
    fun `test getPostById calls repository and returns correct result`() = testScope.runTest {
        whenever(repository.getPostById(1)).thenReturn(post)

        var result: PostEntity? = null
        viewModel.getPostById(1) {
            result = it
        }

        // Advance the dispatcher until all work is complete
        advanceUntilIdle()

        assertThat(result).isEqualTo(post)
    }
}
