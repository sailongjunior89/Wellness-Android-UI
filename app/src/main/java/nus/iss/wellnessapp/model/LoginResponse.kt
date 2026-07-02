package nus.iss.wellnessapp.model

//author: Junior
data class LoginResponse(
    val userId: Long,
    val username: String,
    val email: String,
    val role: String,
    val token: String
)