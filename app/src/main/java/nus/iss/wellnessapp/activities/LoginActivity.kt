package nus.iss.wellnessapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.api.RetrofitClient
import nus.iss.wellnessapp.model.LoginRequest
import nus.iss.wellnessapp.storage.TokenManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import nus.iss.wellnessapp.notification.NotificationHelper
import nus.iss.wellnessapp.notification.NotificationPermissionHelper
import nus.iss.wellnessapp.notification.ReminderScheduler
import nus.iss.wellnessapp.storage.PreferenceHelper

//author: Junior

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() //replace android logo
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Skip login if already authenticated
        if (TokenManager.getToken() != null) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        // Show toast if redirected due to session/token expiry
        if (intent.getBooleanExtra("SESSION_EXPIRED", false)) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
        }

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        btnLogin.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {

        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty()) {
            etUsername.error = "Username is required"
            etUsername.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            etPassword.requestFocus()
            return
        }

        lifecycleScope.launch {

            try {

                Log.d("LOGIN", "Calling login API...")

                val response = RetrofitClient.loginApi.login(
                    LoginRequest(
                        username = username,
                        password = password
                    )
                )

                Log.d("LOGIN", "HTTP Code = ${response.code()}")

                if (response.isSuccessful) {

                    val loginResponse = response.body()

                    if (loginResponse == null) {

                        Toast.makeText(
                            this@LoginActivity,
                            "Server returned an empty response",
                            Toast.LENGTH_LONG
                        ).show()

                        return@launch
                    }

                    Log.d("LOGIN", "User = ${loginResponse.username}")

                    TokenManager.saveToken(loginResponse.token)
                    TokenManager.saveUserId(loginResponse.userId)
                    TokenManager.saveUsername(loginResponse.username)
                    TokenManager.saveEmail(loginResponse.email)

                    Toast.makeText(
                        this@LoginActivity,
                        "Login Successful",
                        Toast.LENGTH_SHORT
                    ).show()

//                    startActivity(
//                        Intent(
//                            this@LoginActivity,
//                            DashboardActivity::class.java
//                        )
//                    )
//
//                    finish()

                    // Tan Pang Wee : Prompt Notification permission before go to dashboard
                    PreferenceHelper.initialize(this@LoginActivity)
                    NotificationHelper.createNotificationChannel(this@LoginActivity)
                    val stepsInterval = PreferenceHelper.getStepsInterval(this@LoginActivity)
                    Toast.makeText(this@LoginActivity, "${stepsInterval}", Toast.LENGTH_SHORT).show()
                    NotificationPermissionHelper.requestOrRun(this@LoginActivity) {
                        val stepsInterval = PreferenceHelper.getStepsInterval(this@LoginActivity)
//                        val waterInterval = PreferenceHelper.getWaterInterval(this)
//                        val moodInterval = PreferenceHelper.getMoodInterval(this)
//                        val sleepHour = PreferenceHelper.getSleepHour(this)
//                        val sleepMinute = PreferenceHelper.getSleepMinute(this)
                        scheduleRemindersAndGoDashboard()
                    }

                } else {

                    val error = response.errorBody()?.string()

                    Log.e("LOGIN", error ?: "Unknown error")

                    Toast.makeText(
                        this@LoginActivity,
                        error ?: "Login Failed",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Log.e("LOGIN", "Exception", e)
                e.printStackTrace()

                Toast.makeText(
                    this@LoginActivity,
                    e.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Tan Pang Wee Notification
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        NotificationPermissionHelper.handleResult(
            this,
            requestCode,
            grantResults
        ) {
            scheduleRemindersAndGoDashboard()
        }
    }

    private fun scheduleRemindersAndGoDashboard() {
        Log.d("PWT", "scheduleRemindersAndGoDashboard")
        ReminderScheduler.scheduleOneTimeStepsReminder(this)
        val stepsInterval = PreferenceHelper.getStepsInterval(this)
        var sleepHr = PreferenceHelper.getSleepHour(this)
        val sleepMin  = PreferenceHelper.getSleepMinute(this)
        Toast.makeText(this, "${stepsInterval} ${sleepHr} ${sleepMin}", Toast.LENGTH_SHORT).show()
        ReminderScheduler.scheduleStepsReminder(this, stepsInterval)
//        ReminderScheduler.scheduleReminderAt(this@LoginActivity, "Sleep", 12, 45)
        ReminderScheduler.scheduleReminderAt(this@LoginActivity, "Sleep", sleepHr, sleepMin)

        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        finish()
    }
}