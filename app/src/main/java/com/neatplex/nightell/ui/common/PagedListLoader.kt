package com.neatplex.nightell.ui.common

import com.neatplex.nightell.domain.util.Page
import com.neatplex.nightell.domain.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue

/** UI state of a cursor-paginated list. */
data class PagedState<out T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val canLoadMore: Boolean = true,
    val error: String? = null,
)

/**
 * Loads a cursor-paginated list page by page and exposes it as [PagedState].
 * Shared by every screen with an infinite list, so the paging rules live in one place.
 *
 * @param idOf returns the cursor of an item; the last item's id is sent as `lastId`.
 * @param fetch loads the page that follows `lastId` (null for the first page).
 */
class PagedListLoader<T>(
    private val scope: CoroutineScope,
    private val idOf: (T) -> Int,
    private val fetch: suspend (lastId: Int?) -> Result<Page<T>>,
) {
    private val _state = MutableStateFlow(PagedState<T>())
    val state: StateFlow<PagedState<T>> = _state.asStateFlow()

    private var job: Job? = null

    /** Loads the next page, unless one is already loading or the list is complete. */
    fun loadNext() {
        val current = _state.value
        if (current.isLoading || !current.canLoadMore) return
        load(lastId = current.items.lastOrNull()?.let(idOf), append = true)
    }

    /**
     * Reloads the list from the first page, cancelling any load in flight.
     * @param showIndicator whether to show the pull-to-refresh indicator.
     */
    fun refresh(showIndicator: Boolean = false) {
        job?.cancel()
        _state.update { it.copy(isRefreshing = showIndicator, canLoadMore = true, error = null) }
        load(lastId = null, append = false)
    }

    /** Edits the loaded items in place, e.g. after deleting one. */
    fun updateItems(transform: (List<T>) -> List<T>) {
        _state.update { it.copy(items = transform(it.items)) }
    }

    private fun load(lastId: Int?, append: Boolean) {
        job = scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = fetch(lastId)) {
                is Result.Success -> _state.update {
                    it.copy(
                        items = if (append) it.items + result.data.items else result.data.items,
                        canLoadMore = result.data.hasMore,
                        isLoading = false,
                        isRefreshing = false,
                    )
                }
                // Keep what is already shown. Stop auto-paging so a failing request
                // isn't retried on every scroll; a refresh starts over.
                is Result.Failure -> _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        canLoadMore = false,
                        error = result.message,
                    )
                }
            }
        }
    }
}
