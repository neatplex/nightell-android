package com.neatplex.nightell.domain.util

/**
 * Outcome of an operation that can fail, e.g. a network call.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()

    data class Failure(
        val message: String,
        val exception: Throwable? = null,
        /** HTTP status code, when the failure came from the server. */
        val code: Int? = null,
    ) : Result<Nothing>()
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Failure -> this
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

fun <T> Result<T>.getOrNull(): T? = (this as? Result.Success)?.data
