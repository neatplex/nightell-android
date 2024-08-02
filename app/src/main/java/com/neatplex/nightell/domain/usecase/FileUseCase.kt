package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.FileUploadResponse
import com.neatplex.nightell.domain.repository.IFileRepository
import java.io.File
import javax.inject.Inject
import com.neatplex.nightell.utils.Result
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class FileUseCase @Inject constructor(private val fileRepository: IFileRepository) {

    suspend fun uploadUserFile(file: File, extension: String): Result<FileUploadResponse> {
        // Convert the file to a request body
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val extensionPart = extension.toRequestBody("text/plain".toMediaTypeOrNull())
        // Additional business logic, if any, can be added here
        return fileRepository.uploadFile(filePart, extensionPart)
    }
}