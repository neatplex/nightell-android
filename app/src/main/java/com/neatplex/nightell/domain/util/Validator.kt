package com.neatplex.nightell.domain.util

import javax.inject.Inject

/**
 * Input validation rules. Pure Kotlin, so it can be unit-tested without Android.
 */
class Validator @Inject constructor() {

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email)

    fun isValidPassword(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH

    fun isValidUsername(username: String): Boolean =
        username.length >= MIN_USERNAME_LENGTH && USERNAME_REGEX.matches(username)

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
        const val MIN_USERNAME_LENGTH = 5

        // Same pattern as android.util.Patterns.EMAIL_ADDRESS.
        val EMAIL_REGEX = Regex(
            "[a-zA-Z0-9+._%\\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
        )
        val USERNAME_REGEX = Regex("^[a-z][a-z0-9_]*$")
    }
}
