package nus.iss.wellnessapp.model

data class ChatResponse(
    val sessionId: Long,
    val messageId: Long,
    val reply: String,
    val timestamp: List<Int>    // Spring returns LocalDateTime as [year,month,day,hour,min,sec,nano]
)
