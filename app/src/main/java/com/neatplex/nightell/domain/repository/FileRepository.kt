package com.neatplex.nightell.domain.repository


import okhttp3.MultipartBody
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.data.dto.FileUploadResponse
import com.neatplex.nightell.data.network.ApiService
import com.neatplex.nightell.utils.handleApiResponse
import okhttp3.RequestBody
import javax.inject.Inject

class FileRepository @Inject constructor(private val apiService: ApiService) : IFileRepository {

    override suspend fun uploadFile(
        file: MultipartBody.Part,
        extension: RequestBody
    ): Result<FileUploadResponse> {

        return try {
            val response = apiService.uploadFile(file, extension)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Failure("Error uploading file", null)
        }
    }
}