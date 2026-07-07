package nus.iss.wellnessapp.model

data class UserProfileRequest(

    val firstName: String,

    val lastName: String,

    val gender: Gender,

    val dateOfBirth: String,

    val address: String,

    val heightCm: Double,

    val weightKg: Double,

    val fitnessGoal: FitnessGoal

)