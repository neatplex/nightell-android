package com.neatplex.nightell.domain.usecase

import com.neatplex.nightell.domain.model.FileType
import com.neatplex.nightell.domain.model.MediaFile
import com.neatplex.nightell.domain.repository.FileRepository
import com.neatplex.nightell.domain.util.Result
import java.io.File
import javax.inject.Inject

/** Checks a file against the upload rules, then uploads it. */
class FileUseCase @Inject constructor(private val fileRepository: FileRepository) {

    /**
     * Returns null when [fileName] and [sizeBytes] satisfy the rules for [type],
     * otherwise a message for the user.
     */
    fun validate(fileName: String, sizeBytes: Long, type: FileType): String? {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when (type) {
            FileType.AUDIO -> when {
                extension != "mp3" -> "Only .mp3 type is allowed for post audio!"
                sizeBytes > MAX_AUDIO_BYTES -> "Audio file must be less than 5MB!"
                else -> null
            }
            FileType.IMAGE -> when {
                extension != "jpg" && extension != "jpeg" -> "Only .jpg type is allowed for post image!"
                sizeBytes > MAX_IMAGE_BYTES -> "Image file must be less than 2MB!"
                else -> null
            }
        }
    }

    suspend fun upload(file: File, type: FileType): Result<MediaFile> =
        fileRepository.upload(file, type)

    private companion object {
        const val MAX_AUDIO_BYTES = 5L * 1024 * 1024
        const val MAX_IMAGE_BYTES = 2L * 1024 * 1024
    }
}
