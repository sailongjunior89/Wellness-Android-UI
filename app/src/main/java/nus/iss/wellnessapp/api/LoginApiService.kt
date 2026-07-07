package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.model.LoginRequest
import nus.iss.wellnessapp.model.LoginResponse
import nus.iss.wellnessapp.model.LogoutResponse
import nus.iss.wellnessapp.model.RegisterRequest
import nus.iss.wellnessapp.model.RegisterResponse
import nus.iss.wellnessapp.model.UserProfileRequest
import nus.iss.wellnessapp.model.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

//author: Junior

interface LoginApiService {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<LogoutResponse>

    @GET("api/profile")
    suspend fun getProfile(): Response<UserProfileResponse>

    @PUT("api/profile")
    suspend fun updateProfile(
        @Body request: UserProfileRequest
    ): Response<UserProfileResponse>
}