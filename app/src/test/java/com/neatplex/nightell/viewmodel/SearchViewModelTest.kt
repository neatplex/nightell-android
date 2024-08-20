package com.neatplex.nightell.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.neatplex.nightell.data.dto.PostCollection
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.IPostRepository
import com.neatplex.nightell.domain.repository.IUserRepository
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.ui.search.SearchViewModel
import com.neatplex.nightell.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class SearchViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var postUseCase: PostUseCase
    private lateinit var userUseCase: UserUseCase
    private lateinit var searchViewModel: SearchViewModel

    @Mock
    private lateinit var postRepository: IPostRepository

    @Mock
    private lateinit var userRepository: IUserRepository

    @Mock
    private lateinit var postsObserver: Observer<List<Post>?>

    @Mock
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        postUseCase = PostUseCase(postRepository)
        userUseCase = UserUseCase(userRepository)
        searchViewModel = SearchViewModel(postUseCase, userUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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

    val posts = listOf(mockPost)

    @Test
    fun `test search success`() = runTest {
        val query = "test"
        `when`(postRepository.search(query, null)).thenReturn(Result.Success(PostCollection(posts)))

        searchViewModel.posts.observeForever(postsObserver)
        searchViewModel.isLoading.observeForever(isLoadingObserver)

        searchViewModel.searchPost(query, null, false)

        verify(isLoadingObserver).onChanged(true)
        verify(postsObserver).onChanged(emptyList())
        verify(postsObserver).onChanged(posts)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test search failure`() = runTest {
        val query = "test"
        `when`(postRepository.search(query, null)).thenReturn(Result.Failure("Error", null))

        searchViewModel.posts.observeForever(postsObserver)
        searchViewModel.isLoading.observeForever(isLoadingObserver)

        searchViewModel.searchPost(query, null, false)

        verify(isLoadingObserver).onChanged(true)
        verify(postsObserver).onChanged(emptyList())
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test search with same query and canLoadMore false`() = runTest {
        val query = "test"
        searchViewModel.canLoadMorePost = false

        searchViewModel.searchPost(query, null, true)

        verify(postsObserver, never()).onChanged(any())
        verify(isLoadingObserver, never()).onChanged(any())
    }

    @Test
    fun `test search with same query and canLoadMore true`() = runTest {
        val query = "test"
        searchViewModel.canLoadMorePost = true
        `when`(postRepository.search(query, null)).thenReturn(Result.Success(PostCollection(posts)))

        searchViewModel.posts.observeForever(postsObserver)
        searchViewModel.isLoading.observeForever(isLoadingObserver)

        searchViewModel.searchPost(query, null, true)

        verify(isLoadingObserver).onChanged(true)
        verify(postsObserver).onChanged(posts)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test search with different query`() = runTest {
        val query = "test"
        `when`(postRepository.search(query, null)).thenReturn(Result.Success(PostCollection(posts)))

        searchViewModel.posts.observeForever(postsObserver)
        searchViewModel.isLoading.observeForever(isLoadingObserver)

        searchViewModel.searchPost(query, null, false)

        verify(isLoadingObserver).onChanged(true)
        verify(postsObserver).onChanged(emptyList())
        verify(postsObserver).onChanged(posts)
        verify(isLoadingObserver).onChanged(false)
    }
}
