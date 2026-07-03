package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.model.ChatMessageResponse
import nus.iss.wellnessapp.model.ChatRequest
import nus.iss.wellnessapp.model.ChatResponse
import nus.iss.wellnessapp.model.ChatSessionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
//Author : Htet Nandar
interface ChatApiService {

    @POST("api/chat/sessions")
    suspend fun createChatSession(
        @Query("title") title: String = "Wellness Chat"
    ): ChatSessionResponse

    @GET("api/chat/sessions")
    suspend fun getSessions(): List<ChatSessionResponse>

    @POST("api/chat/sessions/{sessionId}/messages")
    suspend fun sendMessage(
        @Path("sessionId") sessionId: Long,
        @Body request: ChatRequest
    ): ChatResponse

    @GET("api/chat/sessions/{sessionId}/messages")
    suspend fun getMessages(
        @Path("sessionId") sessionId: Long
    ): List<ChatMessageResponse>
}
