package com.neatplex.nightell.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.neatplex.nightell.data.dto.FileUploadResponse
import com.neatplex.nightell.data.dto.PostDetailResponse
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.repository.IFileRepository
import com.neatplex.nightell.domain.repository.IPostRepository
import com.neatplex.nightell.domain.usecase.FileUseCase
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.upload.UploadViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class UploadViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var postRepository: IPostRepository

    @Mock
    private lateinit var fileRepository: IFileRepository

    @Mock
    private lateinit var uploadStateObserver: Observer<Result<FileUploadResponse>>

    @Mock
    private lateinit var storePostResultObserver: Observer<Result<PostDetailResponse>>

    @Mock
    private lateinit var isLoadingObserver: Observer<Boolean>

    private lateinit var postUseCase: PostUseCase
    private lateinit var fileUseCase: FileUseCase
    private lateinit var uploadViewModel: UploadViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        postUseCase = PostUseCase(postRepository)
        fileUseCase = FileUseCase(fileRepository)
        uploadViewModel = UploadViewModel(postUseCase, fileUseCase)
        uploadViewModel.uploadState.observeForever(uploadStateObserver)
        uploadViewModel.isLoading.observeForever(isLoadingObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        uploadViewModel.uploadState.removeObserver(uploadStateObserver)
        uploadViewModel.isLoading.removeObserver(isLoadingObserver)
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

//    @Test
//    fun `test upload file success`() = runTest {
//        // Arrange
//        val file = File("test_path")
//        val extension = "text"
//        val customFile = CustomFile(extension, id = 1, path = "test_path", user_id = 1)
//        val fileUploadResponse = FileUploadResponse(file = customFile)
//
//        val fileRequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//        val filePart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)
//        val extensionPart = extension.toRequestBody("text/plain".toMediaTypeOrNull())
//
//        `when`(fileRepository.uploadFile(filePart, extensionPart)).thenReturn(Result.Success(fileUploadResponse))
//
//        val latch = CountDownLatch(1)
//        uploadViewModel.uploadState.observeForever {
//            latch.countDown()
//        }
//
//        // Act
//        val result = uploadViewModel.uploadFile(file, extension)
//
//        // Wait for the LiveData to update with increased timeout
//        latch.await(5, TimeUnit.SECONDS) // Wait up to 5 seconds
//
//        // Assert
//        verify(isLoadingObserver).onChanged(true)
//        verify(uploadStateObserver).onChanged(Result.Success(fileUploadResponse))
//        verify(isLoadingObserver).onChanged(false)
//    }
//
//    @Test
//    fun `test upload file failure`() = runTest {
//        val file = File("test_path")
//        val extension = "text"
//        val fileUploadResponse = Result.Failure("Upload failed")
//
//        // Mock the file part and request body creation
//        val fileRequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//        val filePart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)
//        val extensionPart = extension.toRequestBody("text/plain".toMediaTypeOrNull())
//
//        `when`(fileRepository.uploadFile(filePart, extensionPart)).thenReturn(fileUploadResponse)
//
//        uploadViewModel.uploadState.observeForever(uploadStateObserver)
//        uploadViewModel.isLoading.observeForever(isLoadingObserver)
//
//        uploadViewModel.uploadFile(file, extension)
//
//        verify(isLoadingObserver).onChanged(true)
//        verify(uploadStateObserver).onChanged(fileUploadResponse)
//        verify(isLoadingObserver).onChanged(false)
//    }

    @Test
    fun `test upload post success`() = runTest {
        val title = "Test Title"
        val description = "Test Description"
        val audioId = 1
        val imageId = 2
        val postDetailResponse = Result.Success(PostDetailResponse(post = mockPost))

        `when`(postRepository.uploadPost(title, description, audioId, imageId)).thenReturn(postDetailResponse)

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

        `when`(postRepository.uploadPost(title, description, audioId, imageId)).thenReturn(postDetailResponse)

        uploadViewModel.storePostResult.observeForever(storePostResultObserver)
        uploadViewModel.isLoading.observeForever(isLoadingObserver)

        uploadViewModel.uploadPost(title, description, audioId, imageId)

        verify(isLoadingObserver).onChanged(true)
        verify(storePostResultObserver).onChanged(postDetailResponse)
        verify(isLoadingObserver).onChanged(false)
    }
}
