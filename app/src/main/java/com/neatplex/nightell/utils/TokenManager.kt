package com.neatplex.nightell.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("token_preference", Context.MODE_PRIVATE)

    private val TOKEN_KEY = "auth_token"
    private val USER_ID = "user_id"
    private val USER_EMAIL = "user_email"

    fun setToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun setId(id: Int) {
        sharedPreferences.edit().putInt(USER_ID, id).apply()
    }

    fun getId(): Int {
        return sharedPreferences.getInt(USER_ID, 0)
    }

    fun setEmail(email: String) {
        sharedPreferences.edit().putString(USER_EMAIL, email).apply()
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(USER_EMAIL, null)
    }
}