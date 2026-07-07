package nus.iss.wellnessapp.model

//author: Junior
data class RegisterRequest(

    val username: String,

    val email: String,

    val password: String,

    val confirmPassword: String,

    val role:String="USER"

)