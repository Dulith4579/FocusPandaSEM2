package com.example.focuspanda.Data

import retrofit2.http.GET
import retrofit2.http.Query

interface QuoteApiService {
    @GET("quotes")
    suspend fun getQuotes(
        @Query("X-Api-Key") apiKey: String = "i/55KMO1VyEXHiOfM/gi2w==ZY3k5gBga6OD41tb" // Pass API key as query param
    ): List<QuoteResponse>
}