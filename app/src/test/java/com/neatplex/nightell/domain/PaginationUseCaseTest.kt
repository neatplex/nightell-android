package com.neatplex.nightell.domain

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.util.PAGE_SIZE
import com.neatplex.nightell.domain.util.Page
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.testutil.FakePostRepository
import com.neatplex.nightell.testutil.posts
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PaginationUseCaseTest {

    private val repository = FakePostRepository()
    private val useCase = PostUseCase(repository)

    @Test
    fun `a full page may have more after it`() = runTest {
        repository.feedPages[null] = Result.Success(posts(1..PAGE_SIZE))

        val page = (useCase.loadFeed(null) as Result.Success).data

        assertThat(page.items).hasSize(PAGE_SIZE)
        assertThat(page.hasMore).isTrue()
    }

    @Test
    fun `a short page is the last one`() = runTest {
        repository.feedPages[null] = Result.Success(posts(1..3))

        val page = (useCase.loadFeed(null) as Result.Success).data

        assertThat(page.hasMore).isFalse()
    }

    @Test
    fun `failures pass through with their message and code`() = runTest {
        repository.feedPages[null] = Result.Failure("Internal server error!", code = 500)

        assertThat(useCase.loadFeed(null)).isEqualTo(Result.Failure("Internal server error!", code = 500))
    }

    @Test
    fun `page size can be overridden`() {
        assertThat(Page.of(listOf(1, 2), pageSize = 2).hasMore).isTrue()
        assertThat(Page.of(listOf(1), pageSize = 2).hasMore).isFalse()
    }
}
