package nus.iss.wellnessapp.model

data class WellnessRecordResponse(
    val id: Long,
    val userId: Long?,
    val category: String,
    val value: Double,
    val caloriesBurned: Int,
    val unit: String,
    val durationMinutes: Int,
//    val recordDate: String,
    val recordDate: List<Int>, //work around as List due to backend
    val notes: String
)

fun WellnessRecordResponse.dateInt(): Int {
    return recordDate[0] * 10000 +
            recordDate[1] * 100 +
            recordDate[2]
}