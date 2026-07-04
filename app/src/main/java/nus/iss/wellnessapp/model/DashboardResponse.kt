package nus.iss.wellnessapp.model

import com.google.gson.annotations.SerializedName

data class DashboardResponse(

    @SerializedName("username") val username: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("sleepHours") val sleepHours: Double,
    @SerializedName("exerciseMinutes") val exerciseMinutes: Int,
    @SerializedName("waterIntake") val waterIntake: Double,
    @SerializedName("steps") val steps: Double,
    @SerializedName("mood") val mood: String,
    @SerializedName("latestRecommendation") val latestRecommendation: String,
    @SerializedName("avgExerciseMinutes") val avgExerciseMinutes: Int,
    @SerializedName("avgWaterIntake") val avgWaterIntake: Double,
    @SerializedName("avgSleepHours") val avgSleepHours: Double,
    @SerializedName("avgSteps") val avgSteps: Double

)