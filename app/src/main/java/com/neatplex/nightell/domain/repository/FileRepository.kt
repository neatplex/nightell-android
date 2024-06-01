package com.neatplex.nightell.domain.repository


import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.data.dto.FileUploadResponse
import com.neatplex.nightell.data.api.ApiService
import com.neatplex.nightell.utils.handleApiResponse
import javax.inject.Inject

class FileRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun uploadFile(
        file: File,
        extension: String
    ): Result<FileUploadResponse?> {
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val extensionPart = extension.toRequestBody("text/plain".toMediaTypeOrNull())

        return try {
            val response = apiService.uploadFile(filePart, extensionPart)
            handleApiResponse(response)
        } catch (e: Exception) {
            Result.Error("Error uploading file", null)
        }
    }
}