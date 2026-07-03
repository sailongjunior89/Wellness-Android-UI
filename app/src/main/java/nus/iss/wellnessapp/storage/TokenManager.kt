package nus.iss.wellnessapp.storage

// Author: Junior
// Updated: Htet Nandar — added userId storage alongside JWT token

object TokenManager {

    private var token: String? = null
    private var userId: Long   = -1L

    // ── Token ──────────────────────────────────────────────────────────────

    fun saveToken(jwt: String) { token = jwt }
    fun getToken(): String?    = token
    fun clearToken()           { token = null }

    // ── UserId ─────────────────────────────────────────────────────────────

    fun saveUserId(id: Long)  { userId = id }
    fun getUserId(): Long     = userId
    fun clearUserId()         { userId = -1L }

    // ── Clear all (logout) ─────────────────────────────────────────────────

    fun clear() {
        token  = null
        userId = -1L
    }
}
