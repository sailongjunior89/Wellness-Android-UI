package nus.iss.wellnessapp.model

data class DashboardResponse(

    val username: String,

    val sleepHours: Double,

    val exerciseMinutes: Int,

    val waterIntake: Double,

    val steps: Double,

    val mood: String

)