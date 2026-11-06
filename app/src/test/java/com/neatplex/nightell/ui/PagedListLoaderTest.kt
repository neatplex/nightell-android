package com.neatplex.nightell.ui

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.util.Page
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.ui.common.PagedListLoader
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PagedListLoaderTest {

    private val requests = mutableListOf<Int?>()

    private fun pages(vararg results: Pair<Int?, Result<Page<Int>>>): suspend (Int?) -> Result<Page<Int>> {
        val map = results.toMap()
        return { lastId -> requests += lastId; map.getValue(lastId) }
    }

    @Test
    fun `refresh loads the first page and loadNext appends using the last id`() = runTest(UnconfinedTestDispatcher()) {
        val loader = PagedListLoader(backgroundScope, { it }, pages(
            null to Result.Success(Page(listOf(1, 2), hasMore = true)),
            2 to Result.Success(Page(listOf(3), hasMore = false)),
        ))

        loader.refresh()
        loader.loadNext()

        assertThat(loader.state.value.items).containsExactly(1, 2, 3).inOrder()
        assertThat(loader.state.value.canLoadMore).isFalse()
        assertThat(requests).containsExactly(null, 2).inOrder()
    }

    @Test
    fun `loadNext does nothing once the list is complete`() = runTest(UnconfinedTestDispatcher()) {
        val loader = PagedListLoader(backgroundScope, { it }, pages(
            null to Result.Success(Page(listOf(1), hasMore = false)),
        ))

        loader.refresh()
        loader.loadNext()

        assertThat(requests).containsExactly(null)
    }

    @Test
    fun `loadNext does nothing while a page is loading`() = runTest(UnconfinedTestDispatcher()) {
        val gate = CompletableDeferred<Result<Page<Int>>>()
        var calls = 0
        val loader = PagedListLoader(backgroundScope, { it }) { calls++; gate.await() }

        loader.refresh()
        loader.loadNext()
        loader.loadNext()

        assertThat(calls).isEqualTo(1)
        assertThat(loader.state.value.isLoading).isTrue()
    }

    @Test
    fun `a failure keeps the items already shown and stops auto-paging`() = runTest(UnconfinedTestDispatcher()) {
        val loader = PagedListLoader(backgroundScope, { it }, pages(
            null to Result.Success(Page(listOf(1, 2), hasMore = true)),
            2 to Result.Failure("offline"),
        ))

        loader.refresh()
        loader.loadNext()

        val state = loader.state.value
        assertThat(state.items).containsExactly(1, 2)
        assertThat(state.error).isEqualTo("offline")
        assertThat(state.canLoadMore).isFalse()
    }

    @Test
    fun `refresh replaces the items`() = runTest(UnconfinedTestDispatcher()) {
        var first = true
        val loader = PagedListLoader(backgroundScope, { it }) {
            val items = if (first) listOf(1, 2) else listOf(9)
            first = false
            Result.Success(Page(items, hasMore = false))
        }

        loader.refresh()
        loader.refresh()

        assertThat(loader.state.value.items).containsExactly(9)
    }
}
