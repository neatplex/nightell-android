package com.neatplex.nightell.ui

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.usecase.FollowUseCase
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.testutil.FakeFollowRepository
import com.neatplex.nightell.testutil.FakePostRepository
import com.neatplex.nightell.testutil.FakeSessionRepository
import com.neatplex.nightell.testutil.FakeUserRepository
import com.neatplex.nightell.testutil.MainDispatcherRule
import com.neatplex.nightell.testutil.userProfile
import com.neatplex.nightell.ui.navigation.Routes
import com.neatplex.nightell.ui.screens.user.UserProfileViewModel
import org.junit.Rule
import org.junit.Test

class UserProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeUserRepository()
    private val followRepository = FakeFollowRepository()

    private fun viewModel() = UserProfileViewModel(
        SavedStateHandle(mapOf(Routes.ARG_USER_ID to 7)),
        UserUseCase(userRepository, FakeSessionRepository()),
        FollowUseCase(followRepository),
        PostUseCase(FakePostRepository()),
    )

    @Test
    fun `loads the profile for the user in the route`() {
        userRepository.userProfile = Result.Success(userProfile(7))

        val state = viewModel().uiState.value

        assertThat(state.profile?.user?.id).isEqualTo(7)
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun `following updates the button and the follower count`() {
        userRepository.userProfile = Result.Success(userProfile(7, followedByMe = false, followers = 10))
        val viewModel = viewModel()

        viewModel.toggleFollow()

        val profile = viewModel.uiState.value.profile!!
        assertThat(profile.followedByMe).isTrue()
        assertThat(profile.followersCount).isEqualTo(11)
        assertThat(followRepository.calls).containsExactly("follow:7")
    }

    @Test
    fun `a failed follow is rolled back and shows the error`() {
        userRepository.userProfile = Result.Success(userProfile(7, followedByMe = false, followers = 10))
        followRepository.followResult = Result.Failure("offline")
        val viewModel = viewModel()

        viewModel.toggleFollow()

        val state = viewModel.uiState.value
        assertThat(state.profile!!.followedByMe).isFalse()
        assertThat(state.profile!!.followersCount).isEqualTo(10)
        assertThat(state.error).isEqualTo("offline")
    }

    @Test
    fun `unfollowing calls the unfollow endpoint`() {
        userRepository.userProfile = Result.Success(userProfile(7, followedByMe = true))
        val viewModel = viewModel()

        viewModel.toggleFollow()

        assertThat(followRepository.calls).containsExactly("unfollow:7")
        assertThat(viewModel.uiState.value.profile!!.followedByMe).isFalse()
    }
}
