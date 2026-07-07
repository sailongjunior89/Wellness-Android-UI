package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.model.AiRecommendationResponse
import retrofit2.http.GET
import retrofit2.http.POST
// Author: Htet Nandar
interface RecommendationApiService {

    /** GET /api/recommendations/latest — returns 200 or throws HttpException(404) */
    @GET("api/recommendations/latest")
    suspend fun getLatest(): AiRecommendationResponse

    /** POST /api/recommendations/generate — triggers AI generation, returns 201 */
    @POST("api/recommendations/generate")
    suspend fun generate(): AiRecommendationResponse
}
