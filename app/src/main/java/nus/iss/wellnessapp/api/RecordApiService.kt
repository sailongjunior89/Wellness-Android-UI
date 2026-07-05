package nus.iss.wellnessapp.api

//Author: Si Hua
import nus.iss.wellnessapp.model.WellnessRecordRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RecordApiService {
    @POST("api/wellness/records")
    fun addRecord(
        @Body record: WellnessRecordRequest
    ): Call<Void>
}

// Why: Retrofit works from interfaces — you declare the endpoint,
// it generates the networking code. @Body serializes your model to
// JSON; @Header("Authorization") carries the JWT
// because your backend has a JwtAuthenticationFilter
// that rejects unauthenticated requests.
// Check DashboardApiService for the real path prefix
// and whether they prepend "Bearer ".