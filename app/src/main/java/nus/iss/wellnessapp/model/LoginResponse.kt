package nus.iss.wellnessapp.model

data class LoginResponse(
    val userId: Long,
    val username: String,
    val email: String,
    val role: String,
    val token: String
)