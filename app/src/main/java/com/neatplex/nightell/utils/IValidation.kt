package com.neatplex.nightell.utils

interface IValidation {
    fun isValidEmail(email: String): Boolean
    fun isValidPassword(password: String): Boolean
    fun isValidUsername(username: String): Boolean
}