package com.neatplex.nightell.utils

interface ITokenManager {
    fun setToken(token: String)
    fun getToken(): String?
    fun deleteToken()
    fun logoutForce()
    fun setId(id: Int)
    fun getId(): Int
    fun setEmail(email: String)
    fun getEmail(): String?
}