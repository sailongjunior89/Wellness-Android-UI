package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.model.ChartDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*
interface WellnessApiService {
    @GET("api/wellness/{userId}/history")
    fun getHistoryTrends(
        @Path("userId") userId: Int,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Call<ChartDataResponse>
} */

// Cecil

interface WellnessApiService {
    // 1. Removed "/{userId}" from the path mapping
    @GET("api/wellness/history")
    fun getHistoryTrends(
        // 2. Completely removed the @Path("userId") parameter
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Call<ChartDataResponse>
}