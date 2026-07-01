package nus.iss.wellnessapp.model

data class ChatRequest(
    val message: String,
    val userContext: Map<String, String>? = null
)
