package nus.iss.wellnessapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import nus.iss.wellnessapp.api.RetrofitClient
import nus.iss.wellnessapp.databinding.ActivityDashboardBinding
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadDashboard()
    }

    private fun loadDashboard() {

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.apiService.getDashboard(1)

                binding.txtUsername.text =
                    "Welcome, ${response.username}"

                binding.txtMood.text =
                    response.mood

                binding.txtSleep.text =
                    "${response.sleepHours} hrs"

                binding.txtExercise.text =
                    "${response.exerciseMinutes} mins"

                binding.txtWater.text =
                    "${response.waterIntake} L"

                binding.txtSteps.text =
                    "${response.steps.toInt()}"

            } catch (e: Exception) {

                Toast.makeText(
                    this@DashboardActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()

            }

        }

    }
}