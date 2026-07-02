package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.storage.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

//author: Junior
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val builder = chain.request().newBuilder()

        TokenManager.getToken()?.let {
            builder.addHeader(
                "Authorization",
                "Bearer $it"
            )
        }

        return chain.proceed(builder.build())
    }
}