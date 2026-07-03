package nus.iss.wellnessapp.model
// Author : Htet Nandar

data class ChatRequest(
    val message: String,
    val userContext: Map<String, String>? = null
)
