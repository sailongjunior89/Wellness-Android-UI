package nus.iss.wellnessapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.api.DashboardApiService
import nus.iss.wellnessapp.api.RetrofitClient
import nus.iss.wellnessapp.databinding.ActivityDashboardBinding
import nus.iss.wellnessapp.model.DashboardResponse
import nus.iss.wellnessapp.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var apiService: DashboardApiService

    // Cecil
    // UI Elements
    private lateinit var txtUsername: TextView
    private lateinit var txtFullName: TextView
    private lateinit var txtMood: TextView
    private lateinit var txtRecommendationTitle: TextView
    private lateinit var txtRecommendation: TextView
    private lateinit var txtSeeMore: TextView

    private lateinit var txtAvgSteps: TextView
    private lateinit var txtAvgSleep: TextView
    private lateinit var txtAvgWater: TextView
    private lateinit var txtAvgExercise: TextView

    // Wellness Scores
    private lateinit var txtOverallWellnessScores: TextView
    private lateinit var txtStepsWellnessScores: TextView
    private lateinit var txtSleepWellnessScores: TextView
    private lateinit var txtWaterWellnessScores: TextView
    private lateinit var txtExerciseWellnessScores: TextView

    // Daily Stats & Dates
    private lateinit var txtSteps: TextView
    private lateinit var txtStepsDate: TextView
    private lateinit var txtSleep: TextView
    private lateinit var txtSleepDate: TextView
    private lateinit var txtWater: TextView
    private lateinit var txtWaterDate: TextView
    private lateinit var txtExercise: TextView
    private lateinit var txtExerciseDate: TextView


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

        findViewById<Button>(R.id.btnUpdateRecord).setOnClickListener {
            startActivity(Intent(this, ListViewActivity::class.java))
        }

        findViewById<Button>(R.id.btnNotificationSetting).setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        initViews()
        initRetrofit()


        //logout: Junior
        imgProfile = findViewById(R.id.imgProfile)
        imgProfile.setOnClickListener {
            showProfilePopup()
        }
    }

    // Cecil
    private fun initViews() {
        txtUsername = findViewById(R.id.txtUsername)
        txtFullName = findViewById(R.id.txtFullName)
        txtMood = findViewById(R.id.txtMood)
        txtRecommendationTitle = findViewById(R.id.txtRecommendationTitle)
        txtRecommendation = findViewById(R.id.txtRecommendation)
        txtSeeMore = findViewById(R.id.txtSeeMore)

        txtAvgSteps = findViewById(R.id.txtAvgSteps)
        txtAvgSleep = findViewById(R.id.txtAvgSleep)
        txtAvgWater = findViewById(R.id.txtAvgWater)
        txtAvgExercise = findViewById(R.id.txtAvgExercise)

        // Daily Stats & Dates
        txtSteps = findViewById(R.id.txtSteps)
        txtStepsDate = findViewById(R.id.txtStepsDate)
        txtSleep = findViewById(R.id.txtSleep)
        txtSleepDate = findViewById(R.id.txtSleepDate)
        txtWater = findViewById(R.id.txtWater)
        txtWaterDate = findViewById(R.id.txtWaterDate)
        txtExercise = findViewById(R.id.txtExercise)
        txtExerciseDate = findViewById(R.id.txtExerciseDate)

        // Wellness Scores
        txtOverallWellnessScores = findViewById(R.id.txtOverallWellnessScores)
        txtStepsWellnessScores = findViewById(R.id.txtStepsWellnessScores)
        txtSleepWellnessScores = findViewById(R.id.txtSleepWellnessScores)
        txtWaterWellnessScores = findViewById(R.id.txtWaterWellnessScores)
        txtExerciseWellnessScores = findViewById(R.id.txtExerciseWellnessScores)

    }

    private fun initRetrofit() {
        apiService = RetrofitClient.dashboardApi

    }

    // Cecil
    //private fun fetchDashboardData(userId: Int) {
    private fun fetchDashboardData() {
        apiService.getDashboardData().enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    bindDataToUI(data) // Updating API return data into dashboard.
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

        fetchDashboardData()
        loadRecommendation()
    }
    private fun bindDataToUI(data: DashboardResponse) {
        // Welcome Header info
        txtUsername.text = "❤\uFE0F Welcome ❤\uFE0F,  ${data.username}"
        txtFullName.text = data.fullName
        txtMood.text = "Current Mood: ${data.mood}"

        // Daily Stats Group
        txtSteps.text = String.format("\uD83C\uDFC3\u200D♂\uFE0F\u200D➡\uFE0F  %,.0f steps", data.steps)
        txtSleep.text = "\uD83C\uDF1B  ${data.sleepHours} hrs"
        txtWater.text = "\uD83E\uDD5B  ${data.waterIntake} L"
        txtExercise.text = "\uD83C\uDFCB\uFE0F ${data.exerciseMinutes} mins"

        // Historical Trends Group
        txtAvgSteps.text = String.format("%,.0f steps", data.avgSteps)
        //txtAvgSleep.text = "${data.avgSleepHours} hrs"
        txtAvgSleep.text = String.format("%.1f hrs", data.avgSleepHours ?: 0.0)
        txtAvgWater.text = String.format("%.1f L", data.avgWaterIntake ?: 0.0)
        txtAvgExercise.text = "${data.avgExerciseMinutes} mins"

        // --- Wellness Scores Card ---
        txtOverallWellnessScores.text = String.format(Locale.getDefault(),
            "Overall Wellness Score: %d",data.overallWellnessScore ?: 0)

        txtStepsWellnessScores.text = String.format(Locale.getDefault(),
            "Steps Score: %d / 100", data.stepsScore ?: 0)

        txtSleepWellnessScores.text = String.format(Locale.getDefault(),
            "Sleep Duration Score: %d / 100", data.sleepScore ?: 0)

        txtWaterWellnessScores.text = String.format(Locale.getDefault(),
            "Water Score: %d / 100", data.waterScore ?: 0)

        txtExerciseWellnessScores.text = String.format(Locale.getDefault(),
            "Exercise Duration Score: %d / 100", data.exerciseScore ?: 0)

        // --- Recent Activity Stats (Values + Dates) ---
        txtStepsDate.text = data.stepsRecordDate ?: "No record"
        txtSleepDate.text = data.sleepRecordDate ?: "No record"
        txtWaterDate.text = data.waterRecordDate ?: "No record"
        txtExerciseDate.text = data.exerciseRecordDate ?: "No record"

    }


    // ── AI Recommendation (Htet Nandar) ──────────────────────────────────────────
    private fun loadRecommendation() {
        txtRecommendationTitle.text = ""
        txtRecommendation.text = "Loading your personalized wellness tip…"
        txtRecommendation.maxLines = 1
        txtSeeMore.visibility = android.view.View.GONE

        lifecycleScope.launch {
            try {
                val rec = RetrofitClient.recommendationApi.getLatest()
                txtRecommendationTitle.text = rec.title
                txtRecommendation.maxLines = 1
                txtRecommendation.text = rec.recommendation

                // Show "See more" if the last visible line has an ellipsis
                // (lineCount with maxLines=3 is always ≤ 3, so we check getEllipsisCount instead)
                txtRecommendation.post {
                    val layout = txtRecommendation.layout ?: return@post
                    val lastLine = layout.lineCount - 1
                    if (lastLine >= 0 && layout.getEllipsisCount(lastLine) > 0) {
                        txtSeeMore.visibility = android.view.View.VISIBLE
                    }
                }

                // Toggle expand / collapse inline
                txtSeeMore.setOnClickListener {
                    if (txtRecommendation.maxLines == 1) {
                        txtRecommendation.maxLines = Int.MAX_VALUE  // show all
                        txtSeeMore.text = "See less"
                    } else {
                        txtRecommendation.maxLines = 1             // collapse
                        txtSeeMore.text = "See more"
                    }
                }

            } catch (e: HttpException) {
                txtRecommendationTitle.text = ""
                txtSeeMore.visibility = android.view.View.GONE
                txtRecommendation.text = if (e.code() == 503)
                    "AI service is starting up, please check back in a moment."
                else
                    "⚠️ Could not load recommendation (${e.code()})"
            } catch (e: Exception) {
                txtRecommendationTitle.text = ""
                txtSeeMore.visibility = android.view.View.GONE
                txtRecommendation.text = "⚠️ ${e.message}"
            }
        }
    }

    private fun setupBottomNav() { // Ntet
        binding.bottomNav.selectedItemId = R.id.nav_dashboard
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true

                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryTrendActivity::class.java))
                    true
                }

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

        val btnUpdateProfile = view.findViewById<Button>(R.id.btnUpdateProfile)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        txtUsername.text = TokenManager.getUsername()
        txtEmail.text = TokenManager.getEmail()

        btnUpdateProfile.setOnClickListener {

            popup.dismiss()

            val intent = Intent(
                this@DashboardActivity,
                RegisterStep2Activity::class.java
            )

            intent.putExtra("MODE", "UPDATE")

            startActivity(intent)
        }

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

    //logout : Junior
    private fun logout() {

        lifecycleScope.launch {

            try {

                val token = "Bearer ${TokenManager.getToken()}"

                val response =
                    RetrofitClient.loginApi.logout(token)

                if (response.isSuccessful) {

                    TokenManager.clear()

                    Toast.makeText(
                        this@DashboardActivity,
                        "Logout Successful",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this@DashboardActivity,
                            LoginActivity::class.java
                        )
                    )

                    finish()

                } else {

                    Toast.makeText(
                        this@DashboardActivity,
                        "Logout Failed",
                        Toast.LENGTH_LONG
                    ).show()

                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@DashboardActivity,
                    e.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }
}
