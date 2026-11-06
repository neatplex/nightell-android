package com.neatplex.nightell.domain

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.util.Validator
import org.junit.Test

class ValidatorTest {

    private val validator = Validator()

    @Test
    fun `accepts well-formed emails`() {
        assertThat(validator.isValidEmail("someone@example.com")).isTrue()
        assertThat(validator.isValidEmail("first.last+tag@sub.example.org")).isTrue()
    }

    @Test
    fun `rejects malformed emails`() {
        assertThat(validator.isValidEmail("someone")).isFalse()
        assertThat(validator.isValidEmail("someone@")).isFalse()
        assertThat(validator.isValidEmail("someone@example")).isFalse()
        assertThat(validator.isValidEmail("")).isFalse()
    }

    @Test
    fun `password needs at least eight characters`() {
        assertThat(validator.isValidPassword("1234567")).isFalse()
        assertThat(validator.isValidPassword("12345678")).isTrue()
    }

    @Test
    fun `username is lowercase, starts with a letter and has at least five characters`() {
        assertThat(validator.isValidUsername("night_owl1")).isTrue()
        assertThat(validator.isValidUsername("abcd")).isFalse()
        assertThat(validator.isValidUsername("1night")).isFalse()
        assertThat(validator.isValidUsername("NightOwl")).isFalse()
    }
}
