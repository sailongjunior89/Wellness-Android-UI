package nus.iss.wellnessapp.storage

// Author: Junior
// Updated: Htet Nandar — added userId storage alongside JWT token

object TokenManager {

    private var token: String? = null
    private var userId: Long   = -1L

    private var username: String = ""
    private var email: String = ""


    // ── Token ──────────────────────────────────────────────────────────────

    fun saveToken(jwt: String) { token = jwt }
    fun getToken(): String?    = token
    fun clearToken()           { token = null }

    // ── UserId, username and email ─────────────────────────────────────────────────────────────

    fun saveUserId(id: Long)  { userId = id }
    fun getUserId(): Long     = userId

    fun clearUserId()         { userId = -1L }

    fun saveUsername(value: String) {
        username = value
    }

    fun getUsername(): String {
        return username
    }

    fun saveEmail(value: String) {
        email = value
    }

    fun getEmail(): String {
        return email
    }

    // ── Clear all (logout) ─────────────────────────────────────────────────
    // username and email added
    fun clear() {
        token = null
        userId = -1L
        username = ""
        email = ""
    }
}
