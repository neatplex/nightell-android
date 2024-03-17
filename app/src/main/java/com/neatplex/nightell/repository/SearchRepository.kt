package com.neatplex.nightell.repository

import com.neatplex.nightell.dto.PostCollection
import com.neatplex.nightell.network.ApiService
import javax.inject.Inject
import com.neatplex.nightell.util.Result
import com.neatplex.nightell.util.handleApiResponse


class SearchRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun search(query: String) : Result<PostCollection?>{
        return try {
            val response = apiService.search(query)
            handleApiResponse(response)
        }
        catch (e: Exception){
            Result.Error(e.message ?: "An error occurred")
        }
    }
}