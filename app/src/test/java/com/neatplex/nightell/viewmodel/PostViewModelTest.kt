package com.neatplex.nightell.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.neatplex.nightell.data.dto.Likes
import com.neatplex.nightell.data.dto.PostDetailResponse
import com.neatplex.nightell.data.dto.StoreLike
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.model.Like
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.ILikeRepository
import com.neatplex.nightell.domain.repository.LikeRepository
import com.neatplex.nightell.domain.repository.IPostRepository
import com.neatplex.nightell.domain.usecase.LikeUseCase
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.post.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class PostViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var likeRepository: ILikeRepository

    @Mock
    private lateinit var postRepository: IPostRepository


    private lateinit var postViewModel: PostViewModel
    private lateinit var likeUseCase: LikeUseCase
    private lateinit var postUseCase: PostUseCase

    @Mock
    private lateinit var postDetailObserver: Observer<Result<PostDetailResponse?>>

    @Mock
    private lateinit var postUpdateObserver: Observer<Result<PostDetailResponse?>>

    @Mock
    private lateinit var postDeleteObserver: Observer<Result<Unit>>

    @Mock
    private lateinit var likeObserver: Observer<Result<StoreLike?>>

    @Mock
    private lateinit var unlikeObserver: Observer<Result<Unit>>

    @Mock
    private lateinit var showLikesObserver: Observer<Result<Likes?>>

    @Mock
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Mock
    private lateinit var isFetchingObserver: Observer<Boolean>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        likeUseCase = LikeUseCase(likeRepository)
        postUseCase = PostUseCase(postRepository)
        postViewModel = PostViewModel(postUseCase, likeUseCase)
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
    private val like = Like(1,user,"2023-01-01T00:00:00Z",1,1,mockPost)

    @Test
    fun `test getPostDetail success`() = runTest {
        val postDetailResponse = PostDetailResponse(mockPost)
        `when`(postRepository.getPostById(1)).thenReturn(Result.Success(postDetailResponse))

        postViewModel.postDetailResult.observeForever(postDetailObserver)
        postViewModel.isFetching.observeForever(isFetchingObserver)

        postViewModel.getPostDetail(1)

        verify(isFetchingObserver).onChanged(true)
        verify(postDetailObserver).onChanged(Result.Success(postDetailResponse))
        verify(isFetchingObserver).onChanged(false)
    }

    @Test
    fun `test getPostDetail failure`() = runTest {
        `when`(postRepository.getPostById(1)).thenReturn(Result.Failure("Error", null))

        postViewModel.postDetailResult.observeForever(postDetailObserver)
        postViewModel.isFetching.observeForever(isFetchingObserver)

        postViewModel.getPostDetail(1)

        verify(isFetchingObserver).onChanged(true)
        verify(postDetailObserver).onChanged(Result.Failure("Error", null))
        verify(isFetchingObserver).onChanged(false)
    }

    @Test
    fun `test updatePost success`() = runTest {
        val postDetailResponse = PostDetailResponse(mockPost)
        `when`(postRepository.editPost("title", "description",1)).thenReturn(Result.Success(postDetailResponse))

        postViewModel.postUpdateResult.observeForever(postUpdateObserver)
        postViewModel.isLoading.observeForever(isLoadingObserver)

        postViewModel.updatePost(1, "title", "description")

        verify(isLoadingObserver).onChanged(true)
        verify(postUpdateObserver).onChanged(Result.Success(postDetailResponse))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test updatePost failure`() = runTest {
        `when`(postRepository.editPost("title", "description", 1)).thenReturn(Result.Failure("Error", null))

        postViewModel.postUpdateResult.observeForever(postUpdateObserver)
        postViewModel.isLoading.observeForever(isLoadingObserver)

        postViewModel.updatePost(1, "title", "description")

        verify(isLoadingObserver).onChanged(true)
        verify(postUpdateObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test deletePost success`() = runTest {
        `when`(postRepository.deletePost(1)).thenReturn(Result.Success(Unit))

        postViewModel.postDeleteResult.observeForever(postDeleteObserver)
        postViewModel.isLoading.observeForever(isLoadingObserver)

        postViewModel.deletePost(1)

        verify(isLoadingObserver).onChanged(true)
        verify(postDeleteObserver).onChanged(Result.Success(Unit))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test deletePost failure`() = runTest {
        `when`(postRepository.deletePost(1)).thenReturn(Result.Failure("Error", null))

        postViewModel.postDeleteResult.observeForever(postDeleteObserver)
        postViewModel.isLoading.observeForever(isLoadingObserver)

        postViewModel.deletePost(1)

        verify(isLoadingObserver).onChanged(true)
        verify(postDeleteObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test like success`() = runTest {
        val storeLike = StoreLike(like)
        `when`(likeRepository.like(1)).thenReturn(Result.Success(storeLike))

        postViewModel.likeResult.observeForever(likeObserver)
        postViewModel.isLoading.observeForever(isLoadingObserver)

        postViewModel.like(1)

        verify(isLoadingObserver).onChanged(true)
        verify(likeObserver).onChanged(Result.Success(storeLike))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test like failure`() = runTest {
        `when`(likeRepository.like(1)).thenReturn(Result.Failure("Error", null))

        postViewModel.likeResult.observeForever(likeObserver)
        postViewModel.isLoading.observeForever(isLoadingObserver)

        postViewModel.like(1)

        verify(isLoadingObserver).onChanged(true)
        verify(likeObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test showLikes success`() = runTest {
        val likes = Likes(listOf(like))
        `when`(likeRepository.showLikes(1)).thenReturn(Result.Success(likes))

        postViewModel.showLikesResult.observeForever(showLikesObserver)
        postViewModel.isLoading.observeForever(isLoadingObserver)

        postViewModel.showLikes(1)

        verify(isLoadingObserver).onChanged(true)
        verify(showLikesObserver).onChanged(Result.Success(likes))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test showLikes failure`() = runTest {
        `when`(likeRepository.showLikes(1)).thenReturn(Result.Failure("Error", null))

        postViewModel.showLikesResult.observeForever(showLikesObserver)
        postViewModel.isLoading.observeForever(isLoadingObserver)

        postViewModel.showLikes(1)

        verify(isLoadingObserver).onChanged(true)
        verify(showLikesObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test deleteLike success`() = runTest {
        `when`(likeRepository.deleteLike(1)).thenReturn(Result.Success(Unit))

        postViewModel.unlikeResult.observeForever(unlikeObserver)
        postViewModel.isLoading.observeForever(isLoadingObserver)

        postViewModel.deleteLike(1)

        verify(isLoadingObserver).onChanged(true)
        verify(unlikeObserver).onChanged(Result.Success(Unit))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test deleteLike failure`() = runTest {
        `when`(likeRepository.deleteLike(1)).thenReturn(Result.Failure("Error", null))

        postViewModel.unlikeResult.observeForever(unlikeObserver)
        postViewModel.isLoading.observeForever(isLoadingObserver)

        postViewModel.deleteLike(1)

        verify(isLoadingObserver).onChanged(true)
        verify(unlikeObserver).onChanged(Result.Failure("Error", null))
        verify(isLoadingObserver).onChanged(false)
    }
}
