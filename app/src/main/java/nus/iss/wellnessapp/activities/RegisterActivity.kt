package nus.iss.wellnessapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.api.RetrofitClient
import nus.iss.wellnessapp.model.RegisterRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)

        btnRegister.setOnClickListener {
            registerUser()
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {

        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (username.isEmpty()) {
            etUsername.error = "Username is required"
            etUsername.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            etPassword.requestFocus()
            return
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Confirm Password is required"
            etConfirmPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            return
        }

        lifecycleScope.launch {

            try {

                val response = RetrofitClient.loginApi.register(
                    RegisterRequest(
                        username = username,
                        email = email,
                        password = password
                    )
                )

                if (response.isSuccessful) {

                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration Successful",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                } else {

                    val errorMessage = response.errorBody()?.string()

                    Toast.makeText(
                        this@RegisterActivity,
                        errorMessage ?: "Registration Failed",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@RegisterActivity,
                    e.localizedMessage ?: "Network Error",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}