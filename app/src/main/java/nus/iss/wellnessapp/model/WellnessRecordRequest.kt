package nus.iss.wellnessapp.model

//Author: Si Hua
data class WellnessRecordRequest(
    val userId: Long,
    val category: String,        // must exactly match the backend enum name, e.g. "STEPS"
    val value: Double,
    val caloriesBurned: Double? = null,
    val unit: String? = null,
    val durationMinutes: Int? = null,
    val recordDate: String,      // LocalDate parses ISO format: "2026-07-04"
    val notes: String? = null
)

// Why: Retrofit + Gson converts this Kotlin object into the JSON body of POST request.
// Field names must match what Spring backend's DTO expects
// — check the backend's controller/DTO class for the exact names.