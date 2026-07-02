package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.model.LoginRequest
import nus.iss.wellnessapp.model.LoginResponse
import nus.iss.wellnessapp.model.RegisterRequest
import nus.iss.wellnessapp.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
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
}