package com.neatplex.nightell.data.remote

import com.neatplex.nightell.domain.util.Result
import kotlinx.coroutines.CancellationException
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import java.io.IOException

/**
 * Runs a Retrofit call and converts its outcome to a [Result].
 *
 * - A successful response is passed through [transform].
 * - An HTTP error becomes a [Result.Failure] with the server's message and status code.
 * - An [IOException] (offline, timeout) becomes a network failure.
 * - [CancellationException] is rethrown, so a cancelled coroutine stops instead
 *   of being reported as a failed request.
 */
internal suspend fun <T : Any, R> apiCall(
    call: suspend () -> Response<T>,
    transform: (T) -> R,
): Result<R> = try {
    val response = call()
    val body = response.body()
    when {
        !response.isSuccessful -> response.toFailure()
        body == null -> Result.Failure(EMPTY_BODY_MESSAGE, code = response.code())
        else -> Result.Success(transform(body))
    }
} catch (e: CancellationException) {
    throw e
} catch (e: IOException) {
    Result.Failure(e.localizedMessage ?: NETWORK_ERROR_MESSAGE, e)
} catch (e: Exception) {
    Result.Failure(e.localizedMessage ?: UNKNOWN_ERROR_MESSAGE, e)
}

/** Variant of [apiCall] for endpoints that return no body, such as deletes. */
internal suspend fun apiCallNoContent(call: suspend () -> Response<Unit>): Result<Unit> = try {
    val response = call()
    if (response.isSuccessful) Result.Success(Unit) else response.toFailure()
} catch (e: CancellationException) {
    throw e
} catch (e: IOException) {
    Result.Failure(e.localizedMessage ?: NETWORK_ERROR_MESSAGE, e)
} catch (e: Exception) {
    Result.Failure(e.localizedMessage ?: UNKNOWN_ERROR_MESSAGE, e)
}

private fun Response<*>.toFailure(): Result.Failure {
    val message = when (code()) {
        in 400..499 -> errorBody()?.string()?.let(::parseErrorMessage) ?: CLIENT_ERROR_MESSAGE
        in 500..599 -> SERVER_ERROR_MESSAGE
        else -> UNKNOWN_ERROR_MESSAGE
    }
    return Result.Failure(message, code = code())
}

private fun parseErrorMessage(body: String): String? =
    runCatching { Gson().fromJson(body, JsonObject::class.java).get("message").asString }.getOrNull()

private const val CLIENT_ERROR_MESSAGE = "Error occurred, please try again"
private const val SERVER_ERROR_MESSAGE = "Internal server error!"
private const val UNKNOWN_ERROR_MESSAGE = "Something went wrong, please try again later!"
private const val NETWORK_ERROR_MESSAGE = "Network error, please check your connection"
private const val EMPTY_BODY_MESSAGE = "Empty response from server"
