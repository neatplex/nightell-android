package com.neatplex.nightell.utils


sealed class Result<out T> {
    data class Success<out T>(val data: T, val code: Int? = null) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    }

