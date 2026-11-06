package com.neatplex.nightell.data.repository

import com.neatplex.nightell.data.mapper.toDomain
import com.neatplex.nightell.data.remote.api.ApiService
import com.neatplex.nightell.data.remote.apiCall
import com.neatplex.nightell.domain.model.FileType
import com.neatplex.nightell.domain.model.MediaFile
import com.neatplex.nightell.domain.repository.FileRepository
import com.neatplex.nightell.domain.util.Result
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(private val api: ApiService) : FileRepository {

    override suspend fun upload(file: File, type: FileType): Result<MediaFile> {
        val body = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, body)
        val extensionPart = type.apiExtension.toRequestBody("text/plain".toMediaTypeOrNull())
        return apiCall({ api.uploadFile(filePart, extensionPart) }) { it.file.toDomain() }
    }
}
