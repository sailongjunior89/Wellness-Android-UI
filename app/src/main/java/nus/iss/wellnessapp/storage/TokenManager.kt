package nus.iss.wellnessapp.storage

object TokenManager {

    private var token: String? = null

    fun saveToken(jwt: String) {
        token = jwt
    }

    fun getToken(): String? {
        return token
    }

    fun clearToken() {
        token = null
    }
}