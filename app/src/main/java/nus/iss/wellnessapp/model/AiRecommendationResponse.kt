package nus.iss.wellnessapp.model

import com.google.gson.annotations.SerializedName

// Author: Htet Nandar
data class AiRecommendationResponse(
    @SerializedName("id")             val id: Long,
    @SerializedName("title")          val title: String,
    @SerializedName("recommendation") val recommendation: String,
    @SerializedName("generatedAt")    val generatedAt: String?
)
