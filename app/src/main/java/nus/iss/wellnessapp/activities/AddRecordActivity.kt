package nus.iss.wellnessapp.activities
// Author: Si Hua
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.api.RetrofitClient
import nus.iss.wellnessapp.model.WellnessRecordRequest
import nus.iss.wellnessapp.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)

        val etSteps = findViewById<EditText>(R.id.etSteps)
        val etSleep = findViewById<EditText>(R.id.etSleep)
        val etWater = findViewById<EditText>(R.id.etWater)
        val etExercise = findViewById<EditText>(R.id.etExercise)
        val etMood = findViewById<EditText>(R.id.etMood)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etCalories = findViewById<EditText>(R.id.etCalories)

// selectedDate holds the chosen date; starts as today
        var selectedDate = java.time.LocalDate.now()
        etDate.setText(selectedDate.toString())

        etDate.setOnClickListener {
            // DatePickerDialog is Android's built-in calendar popup.
            // Note: the dialog counts months from 0 (January = 0),
            // while LocalDate counts from 1 — hence the +1 and -1 below.
            android.app.DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = java.time.LocalDate.of(year, month + 1, dayOfMonth)
                    etDate.setText(selectedDate.toString())
                },
                selectedDate.year,
                selectedDate.monthValue - 1,
                selectedDate.dayOfMonth
            ).show()
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            Toast.makeText(this, "back tapped", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            // 1. Validate: toIntOrNull returns null for empty/bad input instead of crashing
            val steps = etSteps.text.toString().toIntOrNull()
            val sleep = etSleep.text.toString().toDoubleOrNull()
            val water = etWater.text.toString().toDoubleOrNull()
            val exercise = etExercise.text.toString().toIntOrNull()
            val mood = etMood.text.toString().trim()
            // toDoubleOrNull returns null for empty or invalid input instead of crashing
            val calories = etCalories.text.toString().toDoubleOrNull()

            if (steps == null || sleep == null || water == null ||
                exercise == null || mood.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //2.
            val api = RetrofitClient.recordApi

            // 4. Async call — enqueue runs off the main thread; Android forbids
            //    network calls on the UI thread
            val userId = TokenManager.getUserId()
            val today = selectedDate.toString()

            android.util.Log.d("AddRecord", "userId = $userId")

            val records = listOf(
                WellnessRecordRequest(userId, "steps", steps.toDouble(), unit = "steps", recordDate = today),
                WellnessRecordRequest(userId, "sleep", sleep, unit = "hours", recordDate = today),
                WellnessRecordRequest(userId, "water", water, unit = "liters", recordDate = today),
                WellnessRecordRequest(userId, "exercise", exercise.toDouble(), unit = "minutes",
                    caloriesBurned = calories, durationMinutes = exercise, recordDate = today, notes = mood)
            )

            var completed = 0
            var failed = 0
            records.forEach { record ->
                api.addRecord(record).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) completed++ else failed++
                        showResultWhenDone()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        failed++
                        showResultWhenDone()
                    }

                    fun showResultWhenDone() {
                        if (completed + failed == records.size) {
                            if (failed == 0) {
                                Toast.makeText(
                                    this@AddRecordActivity,
                                    "All records saved!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Toast.makeText(
                                    this@AddRecordActivity,
                                    "$failed of ${records.size} failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
            }
        }
    }
    }

// Why the key pieces: toIntOrNull prevents crashes on bad input;
// enqueue keeps networking off the UI thread
// (Android throws NetworkOnMainThreadException otherwise);
// finish() closes your screen so the dashboard's onResume
// refetch shows the new record.
// The token retrieval is a guess
// — open your teammates' storage package
// and copy exactly how they read the JWT.