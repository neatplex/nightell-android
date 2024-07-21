package com.neatplex.nightell.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.neatplex.nightell.data.dto.FileUploadResponse
import com.neatplex.nightell.data.dto.PostDetailResponse
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.FileRepository
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.upload.UploadViewModel
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
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class UploadViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var postUseCase: PostUseCase

    @Mock
    private lateinit var fileRepository: FileRepository

    @Mock
    private lateinit var uploadStateObserver: Observer<Result<FileUploadResponse>>

    @Mock
    private lateinit var storePostResultObserver: Observer<Result<PostDetailResponse>>

    @Mock
    private lateinit var isLoadingObserver: Observer<Boolean>

    @InjectMocks
    private lateinit var uploadViewModel: UploadViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        uploadViewModel = UploadViewModel(postUseCase, fileRepository)
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

    @Test
    fun `test upload file success`() = runTest {
        val file = File("test_path")
        val extension = "jpg"
        val customFile = CustomFile(extension,1,"test_path", 1)
        val fileUploadResponse = Result.Success(FileUploadResponse(customFile))

        `when`(fileRepository.uploadFile(file, extension)).thenReturn(fileUploadResponse)

        uploadViewModel.uploadState.observeForever(uploadStateObserver)
        uploadViewModel.isLoading.observeForever(isLoadingObserver)

        uploadViewModel.uploadFile(file, extension)

        verify(isLoadingObserver).onChanged(true)
        verify(uploadStateObserver).onChanged(fileUploadResponse)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test upload file failure`() = runTest {
        val file = File("test_path")
        val extension = "jpg"
        val fileUploadResponse = Result.Failure("Upload failed")

        `when`(fileRepository.uploadFile(file, extension)).thenReturn(fileUploadResponse)

        uploadViewModel.uploadState.observeForever(uploadStateObserver)
        uploadViewModel.isLoading.observeForever(isLoadingObserver)

        uploadViewModel.uploadFile(file, extension)

        verify(isLoadingObserver).onChanged(true)
        verify(uploadStateObserver).onChanged(fileUploadResponse)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test upload post success`() = runTest {
        val title = "Test Title"
        val description = "Test Description"
        val audioId = 1
        val imageId = 2
        val postDetailResponse = Result.Success(PostDetailResponse(post = mockPost))

        `when`(postUseCase.uploadPost(title, description, audioId, imageId)).thenReturn(postDetailResponse)

        uploadViewModel.storePostResult.observeForever(storePostResultObserver)
        uploadViewModel.isLoading.observeForever(isLoadingObserver)

        uploadViewModel.uploadPost(title, description, audioId, imageId)

        verify(isLoadingObserver).onChanged(true)
        verify(storePostResultObserver).onChanged(postDetailResponse)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test upload post failure`() = runTest {
        val title = "Test Title"
        val description = "Test Description"
        val audioId = 1
        val imageId = 2
        val postDetailResponse = Result.Failure("Post upload failed")

        `when`(postUseCase.uploadPost(title, description, audioId, imageId)).thenReturn(postDetailResponse)

        uploadViewModel.storePostResult.observeForever(storePostResultObserver)
        uploadViewModel.isLoading.observeForever(isLoadingObserver)

        uploadViewModel.uploadPost(title, description, audioId, imageId)

        verify(isLoadingObserver).onChanged(true)
        verify(storePostResultObserver).onChanged(postDetailResponse)
        verify(isLoadingObserver).onChanged(false)
    }
}