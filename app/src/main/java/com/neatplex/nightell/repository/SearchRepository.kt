package com.neatplex.nightell.repository

import com.neatplex.nightell.data.dto.PostCollection
import com.neatplex.nightell.data.api.ApiService
import javax.inject.Inject
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.handleApiResponse


class SearchRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun search(query: String, lastId: Int?) : Result<PostCollection?>{
        return try {
            val response = apiService.search(query, lastId)
            handleApiResponse(response)
        }
        catch (e: Exception){
            Result.Error(e.message ?: "An error occurred")
        }
    }
}