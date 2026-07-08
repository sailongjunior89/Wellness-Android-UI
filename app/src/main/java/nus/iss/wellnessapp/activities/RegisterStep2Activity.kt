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
import com.google.android.material.textfield.TextInputLayout

import kotlinx.coroutines.launch

import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.api.RetrofitClient

import nus.iss.wellnessapp.model.FitnessGoal
import nus.iss.wellnessapp.model.Gender
import nus.iss.wellnessapp.model.LoginRequest
import nus.iss.wellnessapp.model.UserProfileRequest

import nus.iss.wellnessapp.storage.TokenManager

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class RegisterStep2Activity : AppCompatActivity() {

    //Information from RegisterActivity
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String

    // UI Controls
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etDob: EditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etHeight: TextInputEditText
    private lateinit var etWeight: TextInputEditText

    private lateinit var spGender: Spinner
    private lateinit var spFitnessGoal: Spinner

    private lateinit var layoutPassword: TextInputLayout
    private lateinit var layoutConfirmPassword: TextInputLayout

    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    private lateinit var btnRegister: MaterialButton

    private lateinit var progressBar: ProgressBar

    private val calendar = Calendar.getInstance()

    private var isUpdateMode = false


    // onCreate
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register_step2)

        // Receive data from RegisterActivity
        username = intent.getStringExtra("username") ?: ""

        email = intent.getStringExtra("email") ?: ""

        password = intent.getStringExtra("password") ?: ""

        isUpdateMode = intent.getStringExtra("MODE") == "UPDATE"

        // Controls
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etDob = findViewById(R.id.etDob)
        etAddress = findViewById(R.id.etAddress)
        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)

        spGender = findViewById(R.id.spGender)
        spFitnessGoal = findViewById(R.id.spFitnessGoal)

        layoutPassword = findViewById(R.id.layoutPassword)
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword)

        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnRegister = findViewById(R.id.btnRegister)

        progressBar = findViewById(R.id.progressBar)

        // Initialize
        setupGenderSpinner()

        setupFitnessGoalSpinner()

        setupDatePicker()

        // Update Mode
        if (isUpdateMode) {

            layoutPassword.visibility = View.VISIBLE
            layoutConfirmPassword.visibility = View.VISIBLE

            btnRegister.text = "UPDATE"

            loadProfile()

        } else {

            layoutPassword.visibility = View.GONE
            layoutConfirmPassword.visibility = View.GONE

            btnRegister.text = "SUBMIT"
        }


        // Register OnClickListener
        btnRegister.setOnClickListener {

            if (!validateInput()) {
                return@setOnClickListener
            }

            if (isUpdateMode) {

                updateProfile()

            } else {

                loginAfterRegister()

            }
        }
    }


    // Gender Spinner
    private fun setupGenderSpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            Gender.values()
        )

        spGender.adapter = adapter
    }

    // Fitness Goal Spinner
    private fun setupFitnessGoalSpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            FitnessGoal.values()
        )

        spFitnessGoal.adapter = adapter
    }

    // Date Picker
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

    // Update Date
    private fun updateDate() {

        val sdf = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        etDob.setText(
            sdf.format(calendar.time)
        )

    }


    // Validate Input
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

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dob = LocalDate.parse(etDob.text.toString().trim(), formatter)

        if (dob.isAfter(LocalDate.now())) {

            etDob.error = "Date of birth cannot be in the future"
            etDob.requestFocus()
            return false
        }

        if (etAddress.text.toString().trim().isEmpty()) {

            etAddress.error = "Address is required"
            etAddress.requestFocus()
            return false

        }

        val height = etHeight.text.toString().toDoubleOrNull()

        if (height == null || height <= 0 || height >=272) {

            etHeight.error = "Invalid height"
            etHeight.requestFocus()
            return false

        }

        val weight = etWeight.text.toString().toDoubleOrNull()

        if (weight == null || weight <= 0 || weight >=650) {

            etWeight.error = "Invalid weight"
            etWeight.requestFocus()
            return false

        }

        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Only validate when user wants to change password
        if (password.isNotEmpty()) {

            if (confirmPassword.isEmpty()) {

                etConfirmPassword.error = "Confirm Password is required"
                etConfirmPassword.requestFocus()
                return false
            }

            if (password != confirmPassword) {

                etConfirmPassword.error = "Passwords do not match"
                etConfirmPassword.requestFocus()
                return false
            }
        }

        return true

    }

    private fun showLoading(show: Boolean) {

        progressBar.visibility =
            if (show) View.VISIBLE
            else View.GONE

        btnRegister.isEnabled = !show

    }


    // Login After Registration
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

                    // Save JWT Information
                    TokenManager.saveToken(loginResponse.token)

                    TokenManager.saveUserId(loginResponse.userId)

                    TokenManager.saveUsername(loginResponse.username)

                    TokenManager.saveEmail(loginResponse.email)

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


    // Update Profile
    private fun updateProfile() {

        lifecycleScope.launch {

            try {
                val newPassword = etPassword.text.toString().trim()
                val request = UserProfileRequest(

                    firstName = etFirstName.text.toString().trim(),

                    lastName = etLastName.text.toString().trim(),

                    gender = spGender.selectedItem as Gender,

                    dateOfBirth = etDob.text.toString(),

                    address = etAddress.text.toString().trim(),

                    heightCm = etHeight.text.toString().toDouble(),

                    weightKg = etWeight.text.toString().toDouble(),

                    fitnessGoal = spFitnessGoal.selectedItem as FitnessGoal,

                    newPassword =
                        if (newPassword.isBlank())
                            null
                        else
                            newPassword

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
                        if (isUpdateMode)
                            "Profile updated successfully"
                        else
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

    // Open Dashboard
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

    private fun loadProfile() {

        lifecycleScope.launch {

            showLoading(true)

            try {

                val response =
                    RetrofitClient.loginApi.getProfile()

                showLoading(false)

                if (response.isSuccessful) {

                    val profile = response.body() ?: return@launch

                    etFirstName.setText(profile.firstName)
                    etLastName.setText(profile.lastName)
                    etDob.setText(profile.dateOfBirth)
                    etAddress.setText(profile.address)
                    etHeight.setText(profile.heightCm.toString())
                    etWeight.setText(profile.weightKg.toString())

                    spGender.setSelection(
                        Gender.values().indexOf(profile.gender)
                    )

                    spFitnessGoal.setSelection(
                        FitnessGoal.values().indexOf(profile.fitnessGoal)
                    )

                } else {

                    Toast.makeText(
                        this@RegisterStep2Activity,
                        "Unable to load profile",
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
}