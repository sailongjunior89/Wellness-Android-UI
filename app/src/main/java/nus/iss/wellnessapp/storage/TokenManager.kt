package nus.iss.wellnessapp.storage

//author: Junior
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