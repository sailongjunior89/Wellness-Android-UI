package nus.iss.wellnessapp.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

import kotlinx.coroutines.launch

import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.api.RetrofitClient

import nus.iss.wellnessapp.model.FitnessGoal
import nus.iss.wellnessapp.model.Gender
import nus.iss.wellnessapp.model.LoginRequest
import nus.iss.wellnessapp.model.UserProfileRequest

import nus.iss.wellnessapp.storage.TokenManager

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegisterStep2Activity : AppCompatActivity() {

    //==========================================
    // Information from RegisterActivity
    //==========================================

    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String

    //==========================================
    // UI Controls
    //==========================================

    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etDob: EditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etHeight: TextInputEditText
    private lateinit var etWeight: TextInputEditText

    private lateinit var spGender: Spinner
    private lateinit var spFitnessGoal: Spinner

    private lateinit var btnRegister: MaterialButton

    private lateinit var progressBar: ProgressBar

    private val calendar = Calendar.getInstance()

    //==========================================
    // onCreate
    //==========================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register_step2)

        //------------------------------------------
        // Receive data from RegisterActivity
        //------------------------------------------

        username = intent.getStringExtra("username") ?: ""

        email = intent.getStringExtra("email") ?: ""

        password = intent.getStringExtra("password") ?: ""

        //------------------------------------------
        // Controls
        //------------------------------------------

        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etDob = findViewById(R.id.etDob)
        etAddress = findViewById(R.id.etAddress)
        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)

        spGender = findViewById(R.id.spGender)
        spFitnessGoal = findViewById(R.id.spFitnessGoal)

        btnRegister = findViewById(R.id.btnRegister)

        progressBar = findViewById(R.id.progressBar)

        //------------------------------------------
        // Initialize
        //------------------------------------------

        setupGenderSpinner()

        setupFitnessGoalSpinner()

        setupDatePicker()

        //------------------------------------------
        // Register Button
        //------------------------------------------

        btnRegister.setOnClickListener {

            if (validateInput()) {

                loginAfterRegister()

            }

        }

    }

    //====================================================
    // Gender Spinner
    //====================================================

    private fun setupGenderSpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            Gender.values()
        )

        spGender.adapter = adapter
    }

    //====================================================
    // Fitness Goal Spinner
    //====================================================

    private fun setupFitnessGoalSpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            FitnessGoal.values()
        )

        spFitnessGoal.adapter = adapter
    }

    //====================================================
    // Date Picker
    //====================================================

    private fun setupDatePicker() {

        etDob.setOnClickListener {

            DatePickerDialog(
                this,

                { _, year, month, day ->

                    calendar.set(year, month, day)

                    updateDate()

                },

                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)

            ).show()

        }

    }

    //====================================================
    // Update Date
    //====================================================

    private fun updateDate() {

        val sdf = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        etDob.setText(
            sdf.format(calendar.time)
        )

    }

    //====================================================
    // Validate Input
    //====================================================

    private fun validateInput(): Boolean {

        if (etFirstName.text.toString().trim().isEmpty()) {

            etFirstName.error = "First name is required"
            etFirstName.requestFocus()
            return false

        }

        if (etLastName.text.toString().trim().isEmpty()) {

            etLastName.error = "Last name is required"
            etLastName.requestFocus()
            return false

        }

        if (etDob.text.toString().trim().isEmpty()) {

            etDob.error = "Date of birth is required"
            etDob.requestFocus()
            return false

        }

        if (etAddress.text.toString().trim().isEmpty()) {

            etAddress.error = "Address is required"
            etAddress.requestFocus()
            return false

        }

        val height = etHeight.text.toString().toDoubleOrNull()

        if (height == null || height <= 0) {

            etHeight.error = "Invalid height"
            etHeight.requestFocus()
            return false

        }

        val weight = etWeight.text.toString().toDoubleOrNull()

        if (weight == null || weight <= 0) {

            etWeight.error = "Invalid weight"
            etWeight.requestFocus()
            return false

        }

        return true

    }

    //====================================================
    // Loading
    //====================================================

    private fun showLoading(show: Boolean) {

        progressBar.visibility =
            if (show) View.VISIBLE
            else View.GONE

        btnRegister.isEnabled = !show

    }

    //====================================================
    // Login After Registration
    //====================================================

    private fun loginAfterRegister() {

        lifecycleScope.launch {

            showLoading(true)

            try {

                val response = RetrofitClient.loginApi.login(

                    LoginRequest(
                        username = username,
                        password = password
                    )

                )

                if (response.isSuccessful) {

                    val loginResponse = response.body()

                    if (loginResponse == null) {

                        showLoading(false)

                        Toast.makeText(
                            this@RegisterStep2Activity,
                            "Login response is empty.",
                            Toast.LENGTH_LONG
                        ).show()

                        return@launch
                    }

                    //----------------------------------------
                    // Save JWT Information
                    //----------------------------------------

                    TokenManager.saveToken(loginResponse.token)

                    TokenManager.saveUserId(loginResponse.userId)

                    TokenManager.saveUsername(loginResponse.username)

                    TokenManager.saveEmail(loginResponse.email)

                    //----------------------------------------
                    // Save Profile
                    //----------------------------------------

                    updateProfile()

                } else {

                    showLoading(false)

                    Toast.makeText(
                        this@RegisterStep2Activity,
                        response.errorBody()?.string()
                            ?: "Automatic login failed.",
                        Toast.LENGTH_LONG
                    ).show()

                }

            } catch (e: Exception) {

                showLoading(false)

                Toast.makeText(
                    this@RegisterStep2Activity,
                    e.localizedMessage ?: "Network Error",
                    Toast.LENGTH_LONG
                ).show()

            }

        }

    }

    //====================================================
    // Update Profile
    //====================================================

    private fun updateProfile() {

        lifecycleScope.launch {

            try {

                val request = UserProfileRequest(

                    firstName = etFirstName.text.toString().trim(),

                    lastName = etLastName.text.toString().trim(),

                    gender = spGender.selectedItem as Gender,

                    dateOfBirth = etDob.text.toString(),

                    address = etAddress.text.toString().trim(),

                    heightCm = etHeight.text.toString().toDouble(),

                    weightKg = etWeight.text.toString().toDouble(),

                    fitnessGoal = spFitnessGoal.selectedItem as FitnessGoal

                )

                val response =
                    RetrofitClient.loginApi.updateProfile(request)

                android.util.Log.d("PROFILE", "Code = ${response.code()}")
                android.util.Log.d("PROFILE", "Body = ${response.body()}")
                android.util.Log.d("PROFILE", "Error = ${response.errorBody()?.string()}")

                showLoading(false)

                if (response.isSuccessful) {

                    Toast.makeText(
                        this@RegisterStep2Activity,
                        "Profile created successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    openDashboard()

                } else {

                    Toast.makeText(
                        this@RegisterStep2Activity,
                        response.errorBody()?.string()
                            ?: "Unable to save profile",
                        Toast.LENGTH_LONG
                    ).show()

                }

            } catch (e: Exception) {

                showLoading(false)

                Toast.makeText(
                    this@RegisterStep2Activity,
                    e.localizedMessage ?: "Network Error",
                    Toast.LENGTH_LONG
                ).show()

            }

        }

    }

    //====================================================
    // Open Dashboard
    //====================================================

    private fun openDashboard() {

        val intent = Intent(
            this,
            DashboardActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()

    }

}