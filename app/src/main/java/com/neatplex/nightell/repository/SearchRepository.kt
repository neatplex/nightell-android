package com.neatplex.nightell.repository

import com.neatplex.nightell.dto.PostCollection
import com.neatplex.nightell.network.ApiService
import javax.inject.Inject
import com.neatplex.nightell.util.Result


class SearchRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun search(query: String) : Result<PostCollection?>{
        return try {
            val response = apiService.search(query)
            if (response.isSuccessful) {
                Result.Success(response.body())
            }
            else {
                Result.Error(response.message(), response.code())
            }
        }
        catch (e: Exception){
            Result.Error(e.message ?: "An error occurred")
        }
    }
}