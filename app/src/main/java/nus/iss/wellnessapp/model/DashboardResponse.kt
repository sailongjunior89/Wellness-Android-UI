package nus.iss.wellnessapp.model

import com.google.gson.annotations.SerializedName

// Cecil
data class DashboardResponse(

    @SerializedName("username") val username: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("sleepHours") val sleepHours: Double,
    @SerializedName("exerciseMinutes") val exerciseMinutes: Int,
    @SerializedName("waterIntake") val waterIntake: Double,
    @SerializedName("steps") val steps: Double,
    @SerializedName("mood") val mood: String,

    @SerializedName("latestRecommendation") val latestRecommendation: String,
    @SerializedName("latestRecommendationText") val latestRecommendationText: String,

    @SerializedName("avgExerciseMinutes") val avgExerciseMinutes: Int,
    @SerializedName("avgWaterIntake") val avgWaterIntake: Double,
    @SerializedName("avgSleepHours") val avgSleepHours: Double,
    @SerializedName("avgSteps") val avgSteps: Double,

    @SerializedName("overallWellnessScore") val overallWellnessScore: Int,
    @SerializedName("stepsScore") val stepsScore: Int,
    @SerializedName("sleepScore") val  sleepScore: Int,
    @SerializedName("exerciseScore") val exerciseScore: Int,
    @SerializedName("waterScore") val waterScore: Int,

    @SerializedName("sleepRecordDate") val sleepRecordDate: String,
    @SerializedName("exerciseRecordDate") val exerciseRecordDate: String,
    @SerializedName("waterRecordDate") val waterRecordDate: String,
    @SerializedName("stepsRecordDate") val stepsRecordDate: String,

)