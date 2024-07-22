package com.neatplex.nightell.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.ProfileUseCase
import com.neatplex.nightell.ui.profile.ProfileViewModel
import com.neatplex.nightell.utils.Validation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import com.neatplex.nightell.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var profileUseCase: ProfileUseCase

    @Mock
    private lateinit var postUseCase: PostUseCase

    @Mock
    private lateinit var validation: Validation

    @InjectMocks
    private lateinit var profileViewModel: ProfileViewModel

    @Mock
    private lateinit var profileObserver: Observer<Result<Profile>>

    @Mock
    private lateinit var accountDeleteObserver: Observer<Result<Unit>>

    @Mock
    private lateinit var userUpdatedObserver: Observer<Result<UserUpdated>>

    @Mock
    private lateinit var postsObserver: Observer<List<Post>?>

    @Mock
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        profileViewModel = ProfileViewModel(profileUseCase, postUseCase, validation)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createUser(): User {
        return User(
            bio = "Sample Bio",
            created_at = "2023-07-18",
            email = "user@example.com",
            id = 1,
            is_banned = false,
            name = "John Doe",
            password = "password",
            username = "john_doe"
        )
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
    fun `test fetchProfile success`() = runTest {
        val user = createUser()
        val profile = Profile(followers_count = 100, followings_count = 200, user = user)
        whenever(profileUseCase.profile()).thenReturn(Result.Success(profile))

        profileViewModel.profileData.observeForever(profileObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.fetchProfile()

        verify(isLoadingObserver).onChanged(true)
        verify(profileObserver).onChanged(Result.Success(profile))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test fetchProfile failure`() = runTest {
        whenever(profileUseCase.profile()).thenReturn(Result.Failure("Error", null))

        profileViewModel.profileData.observeForever(profileObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.fetchProfile()

        verify(isLoadingObserver).onChanged(true)
        verify(profileObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test updateProfileName success`() = runTest {
        val user = createUser()
        val userUpdated = UserUpdated(user)
        whenever(profileUseCase.changeProfileName(any())).thenReturn(Result.Success(userUpdated))

        profileViewModel.userUpdatedData.observeForever(userUpdatedObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.updateProfileName("New Name")

        verify(isLoadingObserver).onChanged(true)
        verify(userUpdatedObserver).onChanged(Result.Success(userUpdated))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test updateProfileName failure`() = runTest {
        whenever(profileUseCase.changeProfileName(any())).thenReturn(Result.Failure("Error", null))

        profileViewModel.userUpdatedData.observeForever(userUpdatedObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.updateProfileName("New Name")

        verify(isLoadingObserver).onChanged(true)
        verify(userUpdatedObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test refreshProfile resets posts and allows more loading`() {
        profileViewModel.posts.observeForever(postsObserver)

        profileViewModel.refreshProfile(1)

        assert(profileViewModel.canLoadMore)
        verify(postsObserver, times(2)).onChanged(emptyList())
    }

    @Test
    fun `test updateBioOfUser success`() = runTest {
        val user = createUser()
        val userUpdated = UserUpdated(user)
        whenever(profileUseCase.changeProfileBio(any())).thenReturn(Result.Success(userUpdated))

        profileViewModel.userUpdatedData.observeForever(userUpdatedObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.updateBioOfUser("New Bio")

        verify(isLoadingObserver).onChanged(true)
        verify(userUpdatedObserver).onChanged(Result.Success(userUpdated))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test updateBioOfUser failure`() = runTest {
        whenever(profileUseCase.changeProfileBio(any())).thenReturn(Result.Failure("Error", null))

        profileViewModel.userUpdatedData.observeForever(userUpdatedObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.updateBioOfUser("New Bio")

        verify(isLoadingObserver).onChanged(true)
        verify(userUpdatedObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test updateUsernameOfUser success`() = runTest {
        val user = createUser()
        val userUpdated = UserUpdated(user)
        whenever(profileUseCase.changeProfileUsername(any())).thenReturn(Result.Success(userUpdated))

        profileViewModel.userUpdatedData.observeForever(userUpdatedObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.updateUsernameOfUser("New Username")

        verify(isLoadingObserver).onChanged(true)
        verify(userUpdatedObserver).onChanged(Result.Success(userUpdated))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test updateUsernameOfUser failure`() = runTest {
        whenever(profileUseCase.changeProfileUsername(any())).thenReturn(Result.Failure("Error", null))

        profileViewModel.userUpdatedData.observeForever(userUpdatedObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.updateUsernameOfUser("New Username")

        verify(isLoadingObserver).onChanged(true)
        verify(userUpdatedObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test loadPosts success`() = runTest {
        val posts = listOf(mockPost)
        whenever(postUseCase.loadUserPosts(1,null)).thenReturn(Result.Success(posts))

        profileViewModel.posts.observeForever(postsObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.loadPosts(1, null)

        verify(isLoadingObserver).onChanged(true)
        verify(postsObserver).onChanged(posts)
        verify(isLoadingObserver).onChanged(false)
    }


    @Test
    fun `test loadPosts failure`() = runTest {
        whenever(postUseCase.loadUserPosts(any(), any())).thenReturn(Result.Failure("Error", null))

        profileViewModel.posts.observeForever(postsObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.loadPosts(1, null)

        verify(isLoadingObserver).onChanged(true)
        verify(postsObserver).onChanged(emptyList())
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test deleteAccount success`() = runTest {
        whenever(profileUseCase.deleteAccount()).thenReturn(Result.Success(Unit))

        profileViewModel.accountDeleteResult.observeForever(accountDeleteObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.deleteAccount()

        verify(isLoadingObserver).onChanged(true)
        verify(accountDeleteObserver).onChanged(Result.Success(Unit))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test deleteAccount failure`() = runTest {
        whenever(profileUseCase.deleteAccount()).thenReturn(Result.Failure("Error", null))

        profileViewModel.accountDeleteResult.observeForever(accountDeleteObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.deleteAccount()

        verify(isLoadingObserver).onChanged(true)
        verify(accountDeleteObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test isValidUsername returns validation result`() {
        whenever(validation.isValidUsername(any())).thenReturn(true)

        val result = profileViewModel.isValidUsername("validUsername")

        assert(result)
    }
}
