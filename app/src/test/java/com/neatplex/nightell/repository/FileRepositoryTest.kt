package com.neatplex.nightell.repository

import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.data.dto.FileUploadResponse
import com.neatplex.nightell.domain.model.CustomFile
import com.neatplex.nightell.domain.repository.FileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import com.neatplex.nightell.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import retrofit2.Response

@ExperimentalCoroutinesApi
class FileRepositoryTest {

    private val apiService: ApiService = mock(ApiService::class.java)
    private val fileRepository = FileRepository(apiService)
    val mockFile = MultipartBody.Part.createFormData("file", "test.txt")
    val mockExtension = RequestBody.create(null, "txt")

    @Test
    fun `uploadFile should return failure on exception`() = runTest {
        // Arrange
        `when`(apiService.uploadFile(any(), any())).thenThrow(RuntimeException("Network error"))

        // Act
        val result = fileRepository.uploadFile(mockFile, mockExtension)

        // Assert
        assert(result is Result.Failure)
        assert((result as Result.Failure).message == "Error uploading file")

        // Verify
        verify(apiService).uploadFile(any(), any())
    }

    @Test
    fun `uploadFile should return success on valid response`() = runTest {
        // Arrange
        val customFile = CustomFile("txt", 1, "/path/to/file", 123)
        val response = Response.success(FileUploadResponse(customFile))

        `when`(apiService.uploadFile(any(), any())).thenReturn(response)

        // Act
        val result = fileRepository.uploadFile(mockFile, mockExtension)

        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data?.file == customFile)

        // Verify
        verify(apiService).uploadFile(any(), any())
    }
}