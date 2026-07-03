package nus.iss.wellnessapp.model
// Author : Htet Nandar

data class ChatMessageResponse(
    val id: Long,
    val role: String,     // "user" or "assistant"
    val content: String,
    val createdAt: List<Int>
)
