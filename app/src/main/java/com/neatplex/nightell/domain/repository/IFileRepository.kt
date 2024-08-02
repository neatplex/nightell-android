package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.data.dto.FileUploadResponse
import okhttp3.MultipartBody
import com.neatplex.nightell.utils.Result
import okhttp3.RequestBody

interface IFileRepository {
    suspend fun uploadFile(
        file: MultipartBody.Part,
        extension: RequestBody
    ): Result<FileUploadResponse>
}
