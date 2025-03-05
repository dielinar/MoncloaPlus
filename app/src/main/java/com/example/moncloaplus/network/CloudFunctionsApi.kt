package com.example.moncloaplus.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CloudFunctionsApi {
    @GET("exportMealsToSheets")
    suspend fun exportMeals(
        @Query("date") date: String,
        @Query("day") day: String
    ): Response<ApiResponse>
}
