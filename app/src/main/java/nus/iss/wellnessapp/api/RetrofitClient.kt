package nus.iss.wellnessapp.api

import nus.iss.wellnessapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val BASE_URL = BuildConfig.BASE_URL

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)   // AI responses can take 20-40 s
        .writeTimeout(15, TimeUnit.SECONDS)
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

    val recordApi: RecordApiService by lazy {
        retrofit.create(RecordApiService::class.java)
    }
    /** Login/Logout/Register - Junior */
    val loginApi: LoginApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginApiService::class.java)
    }

    // ── Teammates: add your own ApiService files and register them here ──────
    // val wellnessRecordApi: WellnessRecordApiService by lazy { retrofit.create(WellnessRecordApiService::class.java) }
    // val userApi: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
}
