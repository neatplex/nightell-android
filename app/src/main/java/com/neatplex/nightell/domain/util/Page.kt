package com.neatplex.nightell.domain.util


/** The API returns lists in pages of this size, keyed by the last item's id. */
const val PAGE_SIZE = 10

/**
 * One page of a cursor-paginated list.
 *
 * @property hasMore whether another page may follow. The API has no explicit
 * flag, so a page shorter than [PAGE_SIZE] is treated as the last one.
 */
data class Page<out T>(
    val items: List<T>,
    val hasMore: Boolean,
) {
    companion object {
        fun <T> of(items: List<T>, pageSize: Int = PAGE_SIZE) =
            Page(items, hasMore = items.size >= pageSize)
    }
}
