package com.neatplex.nightell.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.ProfileUseCase
import com.neatplex.nightell.ui.home.HomeViewModel
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
import org.junit.rules.TestRule
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var postUseCase: PostUseCase

    @Mock
    private lateinit var profileUseCase: ProfileUseCase

    @InjectMocks
    private lateinit var homeViewModel: HomeViewModel

    @Mock
    private lateinit var feedObserver: Observer<List<Post>>

    @Mock
    private lateinit var profileObserver: Observer<Result<Profile?>>

    @Mock
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        homeViewModel = HomeViewModel(postUseCase, profileUseCase)
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
    fun `test loadFeed success`() = runTest {
        `when`(postUseCase.loadFeed(null)).thenReturn(Result.Success(posts))

        homeViewModel.feed.observeForever(feedObserver)
        homeViewModel.isLoading.observeForever(isLoadingObserver)

        homeViewModel.loadFeed(null)

        verify(isLoadingObserver).onChanged(true)
        verify(feedObserver).onChanged(posts)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test loadFeed failure`() = runTest {
        // Mock the postUseCase to return a failure result
        `when`(postUseCase.loadFeed(null)).thenReturn(Result.Failure("Error", null))

        // Set up observers
        homeViewModel.feed.observeForever(feedObserver)
        homeViewModel.isLoading.observeForever(isLoadingObserver)

        // Clear initial call to observers
        reset(feedObserver)
        reset(isLoadingObserver)

        // Call the method to be tested
        homeViewModel.loadFeed(null)

        // Verify that loading indicator is shown at the beginning
        verify(isLoadingObserver).onChanged(true)
        // Verify that the feed is set to an empty list on failure
        verify(feedObserver).onChanged(emptyList())
        // Verify that loading indicator is hidden at the end
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test refreshFeed`() = runTest {

        `when`(postUseCase.loadFeed(null)).thenReturn(Result.Success(posts))

        homeViewModel.feed.observeForever(feedObserver)
        homeViewModel.isLoading.observeForever(isLoadingObserver)

        homeViewModel.refreshFeed()

        verify(isLoadingObserver).onChanged(true)
        verify(feedObserver).onChanged(posts)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test fetchProfile success`() = runTest {
        val mockProfile = Profile(1,1,user)
        `when`(profileUseCase.profile()).thenReturn(Result.Success(mockProfile))

        homeViewModel.profileData.observeForever(profileObserver)
        homeViewModel.isLoading.observeForever(isLoadingObserver)

        homeViewModel.fetchProfile()

        verify(isLoadingObserver).onChanged(true)
        verify(profileObserver).onChanged(Result.Success(mockProfile))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test fetchProfile failure`() = runTest {
        `when`(profileUseCase.profile()).thenReturn(Result.Failure("Error", null))

        homeViewModel.profileData.observeForever(profileObserver)
        homeViewModel.isLoading.observeForever(isLoadingObserver)

        homeViewModel.fetchProfile()

        verify(isLoadingObserver).onChanged(true)
        verify(profileObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }

}
