package nus.iss.wellnessapp.model

data class ErrorResponse(
    val timestamp: String? = null,
    val status: Int = 0,
    val error: String? = null,
    val message: String? = null
)