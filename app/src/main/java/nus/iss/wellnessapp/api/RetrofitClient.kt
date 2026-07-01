package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val BASE_URL = BuildConfig.BASE_URL

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Chat sessions and messages — Grace */
    val chatApi: ChatApiService by lazy {
        retrofit.create(ChatApiService::class.java)
    }

    /** Dashboard — Cecil */
    val dashboardApi: DashboardApiService by lazy {
        retrofit.create(DashboardApiService::class.java)
    }

    // ── Teammates: add your own ApiService files and register them here ──────
    // val wellnessRecordApi: WellnessRecordApiService by lazy { retrofit.create(WellnessRecordApiService::class.java) }
    // val userApi: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
}
