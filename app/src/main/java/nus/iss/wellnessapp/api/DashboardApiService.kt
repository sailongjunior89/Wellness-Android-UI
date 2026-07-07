package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.model.DashboardResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/*  // Safe keep
interface DashboardApiService {
    @GET("api/dashboard/{id}")
    fun getDashboardData(
        @Path("id") userId: Int
    ): Call<DashboardResponse>
} */

// Cecil
interface DashboardApiService {
    // 1. Changed URL path from "api/dashboard/{id}" to "api/dashboard/"
    @GET("api/dashboard/")
    fun getDashboardData(): Call<DashboardResponse> // 2. Removed the userId parameter completely
}