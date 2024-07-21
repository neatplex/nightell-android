package com.neatplex.nightell.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.FollowRepository
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.ProfileUseCase
import com.neatplex.nightell.ui.user.UserViewModel
import kotlinx.coroutines.Dispatchers
import com.neatplex.nightell.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.*


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var followRepository: FollowRepository

    @Mock
    private lateinit var profileUseCase: ProfileUseCase

    @Mock
    private lateinit var postUseCase: PostUseCase

    @Mock
    private lateinit var usersListObserver: Observer<Result<Users>>

    @Mock
    private lateinit var followResultObserver: Observer<Result<Unit>>

    @Mock
    private lateinit var unfollowResultObserver: Observer<Result<Unit>>

    @Mock
    private lateinit var postListObserver: Observer<List<Post>>

    @Mock
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Mock
    private lateinit var showUserInfoObserver: Observer<Result<Profile>>


    private lateinit var userViewModel: UserViewModel

    private val user =
        User("", "", "email@example.com", 1, false, "username", "password", "username")
    private val profile = Profile(1, 1, user)
    private val users = Users(listOf(user))
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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userViewModel = UserViewModel(followRepository, profileUseCase, postUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test get user info success`() = runTest {

        val showUserInfoResponse = Result.Success(profile)
        `when`(profileUseCase.getUserProfile(1)).thenReturn(showUserInfoResponse)

        userViewModel.showUserInfoResult.observeForever(showUserInfoObserver)
        userViewModel.isLoading.observeForever(isLoadingObserver)

        userViewModel.getUserInfo(1)

        verify(isLoadingObserver).onChanged(true)
        verify(showUserInfoObserver).onChanged(showUserInfoResponse)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test get user info failure`() = runTest {

        val showUserInfoResponse = Result.Failure("Network Error")
        `when`(profileUseCase.getUserProfile(1)).thenReturn(showUserInfoResponse)

        userViewModel.showUserInfoResult.observeForever(showUserInfoObserver)
        userViewModel.isLoading.observeForever(isLoadingObserver)

        userViewModel.getUserInfo(1)

        verify(isLoadingObserver).onChanged(true)
        verify(showUserInfoObserver).onChanged(showUserInfoResponse)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `fetch user followers on success`() = runTest {
        val userId = 1
        val users = Result.Success(users)

        `when`(followRepository.followers(userId)).thenReturn(users)

        userViewModel.usersList.observeForever(usersListObserver)
        userViewModel.isLoading.observeForever(isLoadingObserver)

        userViewModel.fetchUserFollowers(userId)

        verify(isLoadingObserver).onChanged(true)
        verify(usersListObserver).onChanged(users)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `fetch user followings on success`() = runTest {
        val userId = 1
        val users = Result.Success(users)

        `when`(followRepository.followings(userId)).thenReturn(users)

        userViewModel.usersList.observeForever(usersListObserver)
        userViewModel.isLoading.observeForever(isLoadingObserver)

        userViewModel.fetchUserFollowings(userId)

        verify(isLoadingObserver).onChanged(true)
        verify(usersListObserver).onChanged(users)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `follow user on success`() = runTest {
        val userId = 1
        val friendId = 2
        val result = Result.Success(Unit)

        `when`(followRepository.follow(userId, friendId)).thenReturn(result)

        userViewModel.followResult.observeForever(followResultObserver)
        userViewModel.isLoading.observeForever(isLoadingObserver)

        userViewModel.followUser(userId, friendId)

        verify(isLoadingObserver).onChanged(true)
        verify(followResultObserver).onChanged(result)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `unfollow user on success`() = runTest {
        val userId = 1
        val friendId = 2
        val result = Result.Success(Unit)

        `when`(followRepository.unfollow(userId, friendId)).thenReturn(result)

        userViewModel.unfollowResult.observeForever(unfollowResultObserver)
        userViewModel.isLoading.observeForever(isLoadingObserver)

        userViewModel.unfollowUser(userId, friendId)

        verify(isLoadingObserver).onChanged(true)
        verify(unfollowResultObserver).onChanged(result)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `load posts on success`() = runTest {
        val userId = 1
        val lastPostId = null
        val posts = listOf(mockPost)
        val result = Result.Success(posts)

        `when`(postUseCase.loadUserPosts(userId, lastPostId)).thenReturn(result)

        userViewModel.postList.observeForever(postListObserver)
        userViewModel.isLoading.observeForever(isLoadingObserver)

        userViewModel.loadPosts(userId, lastPostId)

        verify(isLoadingObserver).onChanged(true)
        verify(postListObserver).onChanged(posts)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `load posts on failure`() = runTest {
        val userId = 1
        val lastPostId = null
        val result = Result.Failure("Failed to load posts")

        `when`(postUseCase.loadUserPosts(userId, lastPostId)).thenReturn(result)

        userViewModel.postList.observeForever(postListObserver)
        userViewModel.isLoading.observeForever(isLoadingObserver)

        userViewModel.loadPosts(userId, lastPostId)

        verify(isLoadingObserver).onChanged(true)
        verify(postListObserver).onChanged(emptyList())
        verify(isLoadingObserver).onChanged(false)
    }
}