package com.neatplex.nightell.util

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.navigation.NavController

sealed class Result<out T> {
    data class Success<out T>(val data: T, val code: Int? = null) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    }


@Composable
fun ErrorHandler(
    resultState: LiveData<Result<Any?>>,
    navController: NavController
) {
    val result by resultState.observeAsState()

    when (val result = result) {
        is Result.Success -> {
            result.data.let {
                navController.navigate("dashboard")
            }
        }
        is Result.Error -> {
            if (result.code == 401) {
                // Handle specific error for sign-in screen
                // Assuming you've defined some error handling logic here
            } else if (result.code == 500) {
                // Handle specific error for sign-up screen
                Toast.makeText(
                    LocalContext.current,
                    "Server Internal error, Try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        else -> {}
    }
}
