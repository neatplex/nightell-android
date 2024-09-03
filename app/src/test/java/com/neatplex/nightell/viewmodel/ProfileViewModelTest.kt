package com.neatplex.nightell.viewmodel

import androidx.lifecycle.Observer
import com.neatplex.nightell.data.dto.PostCollection
import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.IPostRepository
import com.neatplex.nightell.domain.repository.IUserRepository
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.ui.profile.ProfileViewModel
import com.neatplex.nightell.utils.IValidation
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
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var userUseCase: UserUseCase
    private lateinit var postUseCase: PostUseCase
    private lateinit var profileViewModel: ProfileViewModel

    @Mock
    private lateinit var validation: IValidation

    @Mock
    private lateinit var profileRepository: IUserRepository

    @Mock
    private lateinit var postRepository: IPostRepository


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
        userUseCase = UserUseCase(profileRepository)
        postUseCase = PostUseCase(postRepository)
        profileViewModel = ProfileViewModel(userUseCase, postUseCase, validation)
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
        whenever(profileRepository.fetchProfile()).thenReturn(Result.Success(profile))

        profileViewModel.profileData.observeForever(profileObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.fetchProfile()

        verify(isLoadingObserver).onChanged(true)
        verify(profileObserver).onChanged(Result.Success(profile))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test fetchProfile failure`() = runTest {
        whenever(profileRepository.fetchProfile()).thenReturn(Result.Failure("Error", null))

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
        whenever(profileRepository.changeProfileName(any())).thenReturn(Result.Success(userUpdated))

        profileViewModel.profileNameUpdatedData.observeForever(userUpdatedObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.updateProfileName("New Name")

        verify(isLoadingObserver).onChanged(true)
        verify(userUpdatedObserver).onChanged(Result.Success(userUpdated))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test updateProfileName failure`() = runTest {
        whenever(profileRepository.changeProfileName(any())).thenReturn(Result.Failure("Error", null))

        profileViewModel.profileNameUpdatedData.observeForever(userUpdatedObserver)
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
        whenever(profileRepository.changeProfileBio(any())).thenReturn(Result.Success(userUpdated))

        profileViewModel.profileNameUpdatedData.observeForever(userUpdatedObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.updateBioOfUser("New Bio")

        verify(isLoadingObserver).onChanged(true)
        verify(userUpdatedObserver).onChanged(Result.Success(userUpdated))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test updateBioOfUser failure`() = runTest {
        whenever(profileRepository.changeProfileBio(any())).thenReturn(Result.Failure("Error", null))

        profileViewModel.profileNameUpdatedData.observeForever(userUpdatedObserver)
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
        whenever(profileRepository.changeProfileUsername(any())).thenReturn(Result.Success(userUpdated))

        profileViewModel.profileNameUpdatedData.observeForever(userUpdatedObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.updateUsernameOfUser("New Username")

        verify(isLoadingObserver).onChanged(true)
        verify(userUpdatedObserver).onChanged(Result.Success(userUpdated))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test updateUsernameOfUser failure`() = runTest {
        whenever(profileRepository.changeProfileUsername(any())).thenReturn(Result.Failure("Error", null))

        profileViewModel.profileNameUpdatedData.observeForever(userUpdatedObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.updateUsernameOfUser("New Username")

        verify(isLoadingObserver).onChanged(true)
        verify(userUpdatedObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test loadPosts success`() = runTest {
        val posts = listOf(mockPost)
        whenever(postRepository.showUserPosts(1,null)).thenReturn(Result.Success(PostCollection(posts)))

        profileViewModel.posts.observeForever(postsObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.loadPosts(1, null)

        verify(isLoadingObserver).onChanged(true)
        verify(postsObserver).onChanged(posts)
        verify(isLoadingObserver).onChanged(false)
    }


    @Test
    fun `test loadPosts failure`() = runTest {
        whenever(postRepository.showUserPosts(any(), any())).thenReturn(Result.Failure("Error", null))

        profileViewModel.posts.observeForever(postsObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.loadPosts(1, null)

        verify(isLoadingObserver).onChanged(true)
        verify(postsObserver).onChanged(emptyList())
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test deleteAccount success`() = runTest {
        whenever(profileRepository.deleteAccount()).thenReturn(Result.Success(Unit))

        profileViewModel.accountDeleteResult.observeForever(accountDeleteObserver)
        profileViewModel.isLoading.observeForever(isLoadingObserver)

        profileViewModel.deleteAccount()

        verify(isLoadingObserver).onChanged(true)
        verify(accountDeleteObserver).onChanged(Result.Success(Unit))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test deleteAccount failure`() = runTest {
        whenever(profileRepository.deleteAccount()).thenReturn(Result.Failure("Error", null))

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
