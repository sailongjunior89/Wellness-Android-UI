package nus.iss.wellnessapp.activities
// Author: Si Hua
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.api.RecordApiService
import nus.iss.wellnessapp.model.WellnessRecordRequest
import nus.iss.wellnessapp.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.view.View
import androidx.core.widget.doAfterTextChanged

class LogCategoryActivity : AppCompatActivity() {

    // One place that defines how each category behaves
    private data class CategoryConfig(
        val title: String,
        val unit: String?,      // sent to the backend
        val hint: String,       // shown in the input box
        val isDecimal: Boolean  // decimal keyboard vs whole numbers
    )

    private val configs = mapOf(
        "sleep"    to CategoryConfig("Sleep 🌛",    "hours",   "Hours slept e.g. 7.5",   true),
        "exercise" to CategoryConfig("Exercise 🏋️", "minutes", "Minutes e.g. 30",        false),
        "mood"     to CategoryConfig("Mood 🙂",     null,      "How do you feel? 0-10",  false),
        "water"    to CategoryConfig("Water 🥛",    "liters",  "Liters e.g. 2.0",        true),
        "steps"    to CategoryConfig("Steps 🏃",    "steps",   "Steps e.g. 8000",        false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_category)

        // Which category is this screen showing? Passed by the picker.
        val category = intent.getStringExtra("category") ?: "steps"
        val config = configs[category]!!

        val etValue = findViewById<EditText>(R.id.etValue)
        findViewById<TextView>(R.id.txtCategoryTitle).text = config.title
        etValue.hint = config.hint
        etValue.inputType = if (config.isDecimal)
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        else
            InputType.TYPE_CLASS_NUMBER

        val txtHelper = findViewById<TextView>(R.id.txtHelper)
        if (category == "mood") {
            txtHelper.visibility = View.VISIBLE
            txtHelper.text = "0–2 Low · 3–5 Content · 6–8 Good · 9–10 Excellent"

            // Live feedback as the user types
            etValue.doAfterTextChanged {
                val v = it.toString().toDoubleOrNull()
                txtHelper.text = when {
                    v == null      -> "0–2 Low · 3–5 Content · 6–8 Good · 9–10 Excellent"
                    v > 10         -> "Max is 10"
                    v <= 2         -> "$v = Low 😞"
                    v <= 5         -> "$v = Content 🙂"
                    v <= 8         -> "$v = Good 😀"
                    else           -> "$v = Excellent 🤩"
                }
            }
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnSaveCategory).setOnClickListener {
            val value = etValue.text.toString().toDoubleOrNull()
            if (value == null) {
                Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (category == "mood" && value > 10) {
                Toast.makeText(this, "Mood must be 0-10", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveRecord(category, value, config)
        }
    }

    private fun saveRecord(category: String, value: Double, config: CategoryConfig) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RecordApiService::class.java)
        val token = "Bearer " + TokenManager.getToken()

        val record = WellnessRecordRequest(
            TokenManager.getUserId(),
            category,
            value,
            unit = config.unit,
            // exercise stores its minutes in durationMinutes too (dashboard reads that field)
            durationMinutes = if (category == "exercise") value.toInt() else null,
            recordDate = java.time.LocalDate.now().toString()
        )

        api.addRecord(token, record).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Backend upsert guarantees this UPDATES today's record if one exists
                    Toast.makeText(this@LogCategoryActivity, "${config.title} saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@LogCategoryActivity, "Save failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@LogCategoryActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}