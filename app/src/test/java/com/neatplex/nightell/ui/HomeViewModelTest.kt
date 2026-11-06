package com.neatplex.nightell.ui

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.testutil.FakePostRepository
import com.neatplex.nightell.testutil.MainDispatcherRule
import com.neatplex.nightell.testutil.posts
import com.neatplex.nightell.ui.screens.home.HomeViewModel
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakePostRepository()

    @Test
    fun `loads the first page of the feed on start`() {
        repository.feedPages[null] = Result.Success(posts(1..10))

        val viewModel = HomeViewModel(PostUseCase(repository))

        assertThat(viewModel.feed.value.items.map { it.id }).isEqualTo((1..10).toList())
        assertThat(viewModel.feed.value.canLoadMore).isTrue()
    }

    @Test
    fun `loadMore appends the next page`() {
        repository.feedPages[null] = Result.Success(posts(1..10))
        repository.feedPages[10] = Result.Success(posts(11..12))
        val viewModel = HomeViewModel(PostUseCase(repository))

        viewModel.loadMore()

        assertThat(viewModel.feed.value.items.map { it.id }).isEqualTo((1..12).toList())
        assertThat(viewModel.feed.value.canLoadMore).isFalse()
    }

    @Test
    fun `refresh starts again from the first page`() {
        repository.feedPages[null] = Result.Success(posts(1..10))
        repository.feedPages[10] = Result.Success(posts(11..12))
        val viewModel = HomeViewModel(PostUseCase(repository))
        viewModel.loadMore()

        viewModel.refresh()

        assertThat(viewModel.feed.value.items).hasSize(10)
        assertThat(viewModel.feed.value.isRefreshing).isFalse()
        assertThat(repository.feedRequests).containsExactly(null, 10, null).inOrder()
    }
}
