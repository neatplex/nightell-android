package com.neatplex.nightell.utils

open class Validation : IValidation {

    override fun isValidEmail(email: String): Boolean {
        // Add your email validation logic here
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun isValidPassword(password: String): Boolean {
        // Add your password validation logic here
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\",.<>?]).{8,}\$")
        //return regex.matches(password) && password.length >= 8
        return password.length >= 8
    }

    override fun isValidUsername(username: String): Boolean {
        val regex = Regex("^[a-z][a-z0-9_]*\$")
        return regex.matches(username) && username.length >= 5
    }
}