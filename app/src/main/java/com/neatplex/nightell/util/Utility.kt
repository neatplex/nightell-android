package com.neatplex.nightell.util

import org.json.JSONObject
import retrofit2.Response
import com.google.gson.Gson

//convert string to object
fun <A> String.fromJson(type: Class<A>) : A{
    return Gson().fromJson(this,type)
}

//convert object to string
fun <A> A.toJson() : String?{
    return Gson().toJson(this)
}

// handle errors
suspend fun <T> handleApiResponse(response: Response<T>): Result<T?> {
    return if (response.isSuccessful) {
        Result.Success(response.body(), response.code())
    } else {

        var errorMessage = ""

        if(response.code() in 400..499){
            val errorBody = response.errorBody()?.string()
            errorMessage = try {
                JSONObject(errorBody).getString("message")
            } catch (e: Exception) {
                "Error occurred, please try again later"
            }

        }else if(response.code() in 500..500){
            errorMessage = "Internal server error!"
        }else {
            errorMessage = "Something is wrong, please try later!"
        }
        Result.Error(errorMessage, response.code())

    }
}