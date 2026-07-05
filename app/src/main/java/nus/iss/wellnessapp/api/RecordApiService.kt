package nus.iss.wellnessapp.api

//Author: Si Hua
import nus.iss.wellnessapp.model.WellnessRecordRequest
import nus.iss.wellnessapp.model.WellnessRecordResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RecordApiService {
    @POST("api/wellness/records")
    fun addRecord(
        @Header("Authorization") token: String,
        @Body record: WellnessRecordRequest
    ): Call<Void>

    // for update tan pang wee
    @GET("api/wellness/{id}")
    suspend fun getRecord(
        @Path("id") id: Long
    ): Response<WellnessRecordResponse>

    @GET("api/wellness/userid/{userId}")
    suspend fun getRecordsByUserId(
        @Path("userId") userId: Long
    ): Response<List<WellnessRecordResponse>>

    @PUT("api/wellness/{id}")
    suspend fun updateRecord(
        @Path("id") id: Long,
        @Body request: WellnessRecordRequest
    ): Response<WellnessRecordResponse>

}



// Why: Retrofit works from interfaces — you declare the endpoint,
// it generates the networking code. @Body serializes your model to
// JSON; @Header("Authorization") carries the JWT
// because your backend has a JwtAuthenticationFilter
// that rejects unauthenticated requests.
// Check DashboardApiService for the real path prefix
// and whether they prepend "Bearer ".