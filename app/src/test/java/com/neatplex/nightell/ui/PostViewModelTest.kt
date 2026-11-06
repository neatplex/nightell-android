package com.neatplex.nightell.ui

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.usecase.CommentUseCase
import com.neatplex.nightell.domain.usecase.LikeUseCase
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.SavedPostUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.testutil.FakeCommentRepository
import com.neatplex.nightell.testutil.FakeLikeRepository
import com.neatplex.nightell.testutil.FakePostRepository
import com.neatplex.nightell.testutil.FakeSavedPostRepository
import com.neatplex.nightell.testutil.FakeSessionRepository
import com.neatplex.nightell.testutil.MainDispatcherRule
import com.neatplex.nightell.testutil.comment
import com.neatplex.nightell.testutil.like
import com.neatplex.nightell.testutil.post
import com.neatplex.nightell.testutil.user
import com.neatplex.nightell.ui.navigation.Routes
import com.neatplex.nightell.ui.screens.post.PostViewModel
import org.junit.Rule
import org.junit.Test

class PostViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val myId = 1
    private val postRepository = FakePostRepository().apply { getPostResult = Result.Success(post(5, user(2))) }
    private val likeRepository = FakeLikeRepository()
    private val commentRepository = FakeCommentRepository()
    private val savedPostRepository = FakeSavedPostRepository()

    private fun viewModel() = PostViewModel(
        SavedStateHandle(mapOf(Routes.ARG_POST_ID to 5)),
        FakeSessionRepository(token = "t", userId = myId),
        PostUseCase(postRepository),
        LikeUseCase(likeRepository),
        CommentUseCase(commentRepository),
        SavedPostUseCase(savedPostRepository),
    )

    @Test
    fun `loads the post and whether I liked it`() {
        likeRepository.likes = Result.Success(listOf(like(id = 9, userId = myId, postId = 5), like(10, 3, 5)))

        val state = viewModel().uiState.value

        assertThat(state.post?.id).isEqualTo(5)
        assertThat(state.likesCount).isEqualTo(2)
        assertThat(state.isLiked).isTrue()
        assertThat(state.likeId).isEqualTo(9)
        assertThat(state.isOwnPost).isFalse()
    }

    @Test
    fun `a missing post shows not found`() {
        postRepository.getPostResult = Result.Failure("gone", code = 404)

        assertThat(viewModel().uiState.value.loadError).isEqualTo("Post not found")
    }

    @Test
    fun `liking updates the count at once and keeps the new like id`() {
        likeRepository.likeResult = Result.Success(like(id = 42, userId = myId, postId = 5))
        val viewModel = viewModel()

        viewModel.toggleLike()

        val state = viewModel.uiState.value
        assertThat(state.isLiked).isTrue()
        assertThat(state.likesCount).isEqualTo(1)
        assertThat(state.likeId).isEqualTo(42)
    }

    @Test
    fun `a failed like is rolled back`() {
        likeRepository.likeResult = Result.Failure("offline")
        val viewModel = viewModel()

        viewModel.toggleLike()

        assertThat(viewModel.uiState.value.isLiked).isFalse()
        assertThat(viewModel.uiState.value.likesCount).isEqualTo(0)
    }

    @Test
    fun `a failed unlike is rolled back`() {
        likeRepository.likes = Result.Success(listOf(like(id = 9, userId = myId, postId = 5)))
        likeRepository.unlikeResult = Result.Failure("offline")
        val viewModel = viewModel()

        viewModel.toggleLike()

        assertThat(viewModel.uiState.value.isLiked).isTrue()
        assertThat(viewModel.uiState.value.likeId).isEqualTo(9)
        assertThat(viewModel.uiState.value.likesCount).isEqualTo(1)
    }

    @Test
    fun `bookmarking saves the post on the device`() {
        val viewModel = viewModel()

        viewModel.toggleSaved()

        assertThat(viewModel.uiState.value.isSaved).isTrue()
        assertThat(viewModel().uiState.value.isSaved).isTrue() // a new screen reads it back
    }

    @Test
    fun `a sent comment appears at the top`() {
        commentRepository.comments = Result.Success(listOf(comment(1)))
        commentRepository.addResult = Result.Success(comment(2))
        val viewModel = viewModel()

        viewModel.sendComment("hello")

        assertThat(viewModel.comments.value.items.map { it.id }).containsExactly(2, 1).inOrder()
    }

    @Test
    fun `deleting the post marks it deleted`() {
        val viewModel = viewModel()

        viewModel.deletePost()

        assertThat(viewModel.uiState.value.isDeleted).isTrue()
    }
}
