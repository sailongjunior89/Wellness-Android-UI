package nus.iss.wellnessapp.activities
// author : Tan Pang Wee
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.api.RetrofitClient
import nus.iss.wellnessapp.model.WellnessRecordRequest
import android.app.DatePickerDialog
import android.content.Intent
import java.util.Calendar
import nus.iss.wellnessapp.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recordId = intent.getLongExtra("recordId", -1L)
        getRecord(recordId)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, ListViewActivity::class.java)
            startActivity(intent)
        }

        binding.etRecordDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnGetRecord.isGone = true

        binding.btnUpdateRecord.setOnClickListener {
            val position = intent.getIntExtra("position", -1)
            updateRecord()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun getRecord(recordId: Long) {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.recordApi.getRecord(recordId)
                Log.d("API", "HTTP Code = ${response.code()}")
                if (response.isSuccessful) {

                    val record = response.body()
                    if (record != null) {
                        binding.txtRecordId.text = record.id.toString()
                        binding.etCategory.setText( record.category.uppercase())
                        binding.etValue.setText(record.value.toString())
                        binding.etCalories.setText(record.caloriesBurned.toString())
                        binding.etUnit.setText(record.unit)
                        binding.etDuration.setText(record.durationMinutes.toString())

                        val rawDate = "${record.recordDate[0]}-${record.recordDate[1]}-${record.recordDate[2]}"
                        val formattedDate = formatDate(rawDate)
                        binding.etRecordDate.setText(formattedDate)
//                        binding.etRecordDate.setText(
//                            "${record.recordDate[0]}-${record.recordDate[1]}-${record.recordDate[2]}"
//                        )
                        binding.etNotes.setText(record.notes)

                        if (record.category == "mood" || record.category == "water" ||
                            record.category == "sleep") {
                            binding.etCalories.isGone = true
                            binding.etCaloriesLabel.isGone = true
                            binding.etDuration.isGone = true
                            binding.etDurationLabel.isGone = true
                        }

                        binding.imageView.setImageResource(0)
                        when (record.category.lowercase()) {
                            "sleep" -> binding.imageView.setImageResource(R.drawable.app_img_sleep)
                            "water" -> binding.imageView.setImageResource(R.drawable.app_img_water)
                            "steps" -> binding.imageView.setImageResource(R.drawable.app_img_steps)
                            "exercise" -> binding.imageView.setImageResource(R.drawable.app_img_exercise)
                            "mood" -> binding.imageView.setImageResource(R.drawable.app_img_mood)
                            else -> binding.imageView.setImageResource(R.drawable.app_img_wellness)
                        }
                    }
                } else {
                    Log.e("API", response.errorBody()?.string() ?: "")
                }

            } catch (e: Exception) {
                Log.e("API", e.message ?: "")
            } finally {
                delay(500)
                showLoading(false)
            }
        }
    }

    private fun updateRecord() {

        val rawDate = binding.etRecordDate.text.toString()

        val formattedDate = formatDate(rawDate)
        binding.etRecordDate.setText(formattedDate)

        val request = WellnessRecordRequest(
            userId = 1L,
            category = binding.etCategory.text.toString().lowercase(),
            value = binding.etValue.text.toString().toDouble(),
            caloriesBurned = binding.etCalories.text.toString().toDouble(),
            unit = binding.etUnit.text.toString(),
            durationMinutes = binding.etDuration.text.toString().toInt(),
            recordDate= formattedDate,
            notes = binding.etNotes.text.toString()
        )
        val recordId = binding.txtRecordId.text.toString().toLong()

        callUpdateApi(recordId, request)
    }

    private fun callUpdateApi(
        recordId: Long,
        request: WellnessRecordRequest
    ) {

        lifecycleScope.launch {
            showLoading(true)
            try {

                val response = RetrofitClient.recordApi.updateRecord(recordId, request)

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@EditActivity,
                        "Record updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(
                        this@EditActivity,
                        "Update failed: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@EditActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                delay(500)
                showLoading(false)
            }
        }
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                val date = "%04d-%02d-%02d".format(
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay
                )

                binding.etRecordDate.setText(date)

            },
            year,
            month,
            day
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun formatDate(rawDate: String): String {

        val formattedDate = rawDate
            .split("-")
            .let {
                "%04d-%02d-%02d".format(
                    it[0].toInt(),
                    it[1].toInt(),
                    it[2].toInt()
                )
            }
        return formattedDate
    }
}
