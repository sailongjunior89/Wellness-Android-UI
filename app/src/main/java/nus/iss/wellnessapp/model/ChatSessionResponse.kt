package nus.iss.wellnessapp.model
// Author : Htet Nandar

data class ChatSessionResponse(
    val id: Long,
    val title: String,
    val active: Boolean,
    val createdAt: List<Int>,   // Spring returns LocalDateTime as [year,month,day,hour,min,sec,nano]
    val messageCount: Int
)
