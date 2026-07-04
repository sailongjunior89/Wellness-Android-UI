package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.model.DashboardResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface DashboardApiService {
    @GET("api/dashboard/{id}")
    fun getDashboardData(
        @Path("id") userId: Int
    ): Call<DashboardResponse>
}