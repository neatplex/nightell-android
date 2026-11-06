package com.neatplex.nightell.domain

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.model.FileType
import com.neatplex.nightell.domain.model.LocalFile
import com.neatplex.nightell.domain.model.MediaFile
import com.neatplex.nightell.domain.repository.FileRepository
import com.neatplex.nightell.domain.usecase.FileUseCase
import com.neatplex.nightell.domain.util.Result
import org.junit.Test

class FileUseCaseTest {

    private val useCase = FileUseCase(object : FileRepository {
        override suspend fun upload(file: LocalFile, type: FileType): Result<MediaFile> = error("not used")
    })
    private val mb = 1024L * 1024

    @Test
    fun `accepts mp3 audio up to 5 MB`() {
        assertThat(useCase.validate("story.MP3", 5 * mb, FileType.AUDIO)).isNull()
    }

    @Test
    fun `rejects other audio formats and large files`() {
        assertThat(useCase.validate("story.wav", mb, FileType.AUDIO)).contains(".mp3")
        assertThat(useCase.validate("story.mp3", 5 * mb + 1, FileType.AUDIO)).contains("5MB")
    }

    @Test
    fun `accepts jpg and jpeg images up to 2 MB`() {
        assertThat(useCase.validate("cover.jpg", 2 * mb, FileType.IMAGE)).isNull()
        assertThat(useCase.validate("cover.JPEG", mb, FileType.IMAGE)).isNull()
    }

    @Test
    fun `rejects other image formats and large images`() {
        assertThat(useCase.validate("cover.png", mb, FileType.IMAGE)).contains(".jpg")
        assertThat(useCase.validate("cover.jpg", 2 * mb + 1, FileType.IMAGE)).contains("2MB")
    }
}
