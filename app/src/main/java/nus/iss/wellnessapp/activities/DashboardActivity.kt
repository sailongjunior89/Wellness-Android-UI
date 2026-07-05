package nus.iss.wellnessapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.api.RetrofitClient
import nus.iss.wellnessapp.databinding.ActivityDashboardBinding

import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import nus.iss.wellnessapp.api.DashboardApiService
import nus.iss.wellnessapp.model.DashboardResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import nus.iss.wellnessapp.activities.LoginActivity
import nus.iss.wellnessapp.storage.TokenManager
import android.view.LayoutInflater
import android.view.Gravity
import android.widget.PopupWindow
import nus.iss.wellnessapp.model.LogoutResponse

class DashboardActivity : AppCompatActivity() {

    private lateinit var apiService: DashboardApiService

    // UI Elements
    private lateinit var txtUsername: TextView
    private lateinit var txtFullName: TextView
    private lateinit var txtMood: TextView
    private lateinit var txtRecommendation: TextView
    private lateinit var txtSteps: TextView
    private lateinit var txtSleep: TextView
    private lateinit var txtWater: TextView
    private lateinit var txtExercise: TextView
    private lateinit var txtAvgSteps: TextView
    private lateinit var txtAvgSleep: TextView
    private lateinit var txtAvgWater: TextView
    private lateinit var txtAvgExercise: TextView

    //logout: Junior
    private lateinit var imgProfile: ShapeableImageView

    private val username: String
        get() = TokenManager.getUsername()

    private val email: String
        get() = TokenManager.getEmail()

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater) //Ntet
        setContentView(binding.root) //Ntet

        // Setup bottom navigation using the binding instance
        setupBottomNav() //Ntet

        //setContentView(R.layout.activity_dashboard)
        //Author: Si Hua
        findViewById<Button>(R.id.btnAddRecord).setOnClickListener {
            startActivity(Intent(this, CategoryPickerActivity::class.java))
        }

        initViews()
        initRetrofit()
        fetchDashboardData(userId = 1) // Fetching data for user ID 1 as seen in Postman

        //logout: Junior
        imgProfile = findViewById(R.id.imgProfile)

        imgProfile.setOnClickListener {
            showProfilePopup()
        }
    }

    private fun initViews() {
        txtUsername = findViewById(R.id.txtUsername)
        txtFullName = findViewById(R.id.txtFullName)
        txtMood = findViewById(R.id.txtMood)
        txtRecommendation = findViewById(R.id.txtRecommendation)
        txtSteps = findViewById(R.id.txtSteps)
        txtSleep = findViewById(R.id.txtSleep)
        txtWater = findViewById(R.id.txtWater)
        txtExercise = findViewById(R.id.txtExercise)
        txtAvgSteps = findViewById(R.id.txtAvgSteps)
        txtAvgSleep = findViewById(R.id.txtAvgSleep)
        txtAvgWater = findViewById(R.id.txtAvgWater)
        txtAvgExercise = findViewById(R.id.txtAvgExercise)
    }

    private fun initRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // Use 10.0.2.2 for Localhost inside Android Emulator
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(DashboardApiService::class.java)
    }

    private fun fetchDashboardData(userId: Int) {
        apiService.getDashboardData(userId).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    bindDataToUI(data)
                } else {
                    Toast.makeText(this@DashboardActivity, "Failed to parse data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fetchDashboardData(userId = 1)
    }
    private fun bindDataToUI(data: DashboardResponse) {
        // Welcome Header info
        txtUsername.text = "❤\uFE0F Welcome ❤\uFE0F,  ${data.username}"
        txtFullName.text = data.fullName
        txtMood.text = "Current Mood: ${data.mood}"
        txtRecommendation.text = data.latestRecommendation

        // Daily Stats Group
        txtSteps.text = String.format("\uD83C\uDFC3\u200D♂\uFE0F\u200D➡\uFE0F  %,.0f steps", data.steps)
        txtSleep.text = "\uD83C\uDF1B  ${data.sleepHours} hrs"
        txtWater.text = "\uD83E\uDD5B  ${data.waterIntake} L"
        txtExercise.text = "\uD83C\uDFCB\uFE0F ${data.exerciseMinutes} mins"

        // Historical Trends Group
        txtAvgSteps.text = String.format("%,.0f steps", data.avgSteps)
        txtAvgSleep.text = "${data.avgSleepHours} hrs"
        txtAvgWater.text = String.format("%.1f L", data.avgWaterIntake ?: 0.0)
        txtAvgExercise.text = "${data.avgExerciseMinutes} mins"
    }

    private fun setupBottomNav() { // Ntet
        binding.bottomNav.selectedItemId = R.id.nav_dashboard
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true

                /*R.id.nav_dashboard ->{
                    startActivity(Intent(this, DashboardActivity::class.java))// for dashboard
                    true
                }*/
                R.id.nav_chat -> {
                    startActivity(Intent(this, ChatActivity::class.java))
                    false  // don't highlight
                }
                else -> false
            }
        }
    }

    //profile showing : Junior
    private fun showProfilePopup() {

        val view = LayoutInflater.from(this)
            .inflate(R.layout.profile_popup, null)

        val popup = PopupWindow(
            view,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val txtUsername = view.findViewById<TextView>(R.id.txtPopupUsername)
        val txtEmail = view.findViewById<TextView>(R.id.txtPopupEmail)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        txtUsername.text = TokenManager.getUsername()
        txtEmail.text = TokenManager.getEmail()

        btnLogout.setOnClickListener {
            popup.dismiss()
            logout()
        }

        popup.elevation = 20f

        // Allow closing by tapping outside
        popup.isOutsideTouchable = true
        popup.setBackgroundDrawable(android.graphics.drawable.ColorDrawable())

        // Measure popup width
        view.measure(
            android.view.View.MeasureSpec.UNSPECIFIED,
            android.view.View.MeasureSpec.UNSPECIFIED
        )

        val popupWidth = view.measuredWidth

        // Align popup below the avatar, right edges aligned
        popup.showAsDropDown(
            imgProfile,
            imgProfile.width - popupWidth,
            8
        )
    }

    private fun logout() {

        val token = TokenManager.getToken()

        if (token == null) {

            TokenManager.clear()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()

            return
        }

        RetrofitClient.loginApi.logout("Bearer $token")
            .enqueue(object : Callback<LogoutResponse> {

                override fun onResponse(
                    call: Call<LogoutResponse>,
                    response: Response<LogoutResponse>
                ) {

                    TokenManager.clear()

                    Toast.makeText(
                        this@DashboardActivity,
                        "Logout successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        this@DashboardActivity,
                        LoginActivity::class.java
                    )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()
                }

                override fun onFailure(
                    call: Call<LogoutResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@DashboardActivity,
                        "Unable to logout",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
