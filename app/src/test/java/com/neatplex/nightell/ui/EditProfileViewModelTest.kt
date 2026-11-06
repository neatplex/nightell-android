package com.neatplex.nightell.ui

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.model.LocalFile
import com.neatplex.nightell.domain.usecase.FileUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.Validator
import com.neatplex.nightell.testutil.FakeEditableUserRepository
import com.neatplex.nightell.testutil.FakeFileRepository
import com.neatplex.nightell.testutil.FakeSessionRepository
import com.neatplex.nightell.testutil.MainDispatcherRule
import com.neatplex.nightell.testutil.user
import com.neatplex.nightell.ui.screens.profile.EditProfileEvent
import com.neatplex.nightell.ui.screens.profile.EditProfileViewModel
import com.neatplex.nightell.ui.screens.profile.ProfileField
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class EditProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeEditableUserRepository()
    private val fileRepository = FakeFileRepository()
    private val viewModel = EditProfileViewModel(
        UserUseCase(userRepository, FakeSessionRepository()),
        FileUseCase(fileRepository),
        Validator(),
    )

    @Test
    fun `a saved field emits the updated user once`() = runTest {
        userRepository.updateResult = Result.Success(user(1, username = "new_name"))

        viewModel.updateUsername("new_name")

        assertThat(viewModel.events.first())
            .isEqualTo(EditProfileEvent.Saved(ProfileField.USERNAME, user(1, username = "new_name")))
        assertThat(viewModel.uiState.value.saving).isEmpty()
    }

    @Test
    fun `a failed save shows the error and asks to restore the field`() = runTest {
        userRepository.updateResult = Result.Failure("Username taken", code = 422)

        viewModel.updateUsername("taken")

        assertThat(viewModel.events.first()).isEqualTo(EditProfileEvent.SaveFailed(ProfileField.USERNAME))
        assertThat(viewModel.uiState.value.error).isEqualTo("Username taken")
    }

    @Test
    fun `an invalid image is rejected before anything is copied or uploaded`() {
        var copied = false

        viewModel.onImageChosen("photo.png", 1024) { copied = true; null }

        assertThat(copied).isFalse()
        assertThat(fileRepository.uploaded).isEmpty()
        assertThat(viewModel.uiState.value.error).contains(".jpg")
    }

    @Test
    fun `a valid image is uploaded and set as the profile image`() = runTest {
        viewModel.onImageChosen("photo.jpg", 1024) { LocalFile("/cache/photo.jpg", "photo.jpg") }

        assertThat(viewModel.events.first()).isInstanceOf(EditProfileEvent.Saved::class.java)
        assertThat(fileRepository.uploaded.single().name).isEqualTo("photo.jpg")
        assertThat(userRepository.imageIdSet).isEqualTo(77)
    }
}
