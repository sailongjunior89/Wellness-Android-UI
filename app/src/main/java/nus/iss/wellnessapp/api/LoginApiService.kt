package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.model.LoginRequest
import nus.iss.wellnessapp.model.LoginResponse
import nus.iss.wellnessapp.model.LogoutResponse
import nus.iss.wellnessapp.model.RegisterRequest
import nus.iss.wellnessapp.model.RegisterResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

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
    fun logout(
        @Header("Authorization") token: String
    ): Call<LogoutResponse>
}