package nus.iss.wellnessapp.model

data class ChatMessageResponse(
    val id: Long,
    val role: String,     // "user" or "assistant"
    val content: String,
    val createdAt: List<Int>
)
