package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.data.dto.FileUploadResponse
import com.neatplex.nightell.domain.repository.FileRepository
import java.io.File
import javax.inject.Inject
import com.neatplex.nightell.utils.Result

class FileUseCase @Inject constructor(private val fileRepository: FileRepository) {
    suspend fun uploadUserFile(file: File, extension: String): Result<FileUploadResponse> {
        // Additional business logic, if any, can be added here
        return fileRepository.uploadFile(file, extension)
    }
}