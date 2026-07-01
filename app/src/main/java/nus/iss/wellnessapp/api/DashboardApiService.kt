package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.model.DashboardResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DashboardApiService {

    @GET("api/dashboard/{id}")
    suspend fun getDashboard(
        @Path("id") id: Long
    ): DashboardResponse
}
