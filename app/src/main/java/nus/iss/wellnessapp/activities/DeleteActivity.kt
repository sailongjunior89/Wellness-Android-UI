package nus.iss.wellnessapp.activities

import android.content.Intent
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
import nus.iss.wellnessapp.databinding.ActivityDeleteBinding
import nus.iss.wellnessapp.model.WellnessRecordResponse

class DeleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteBinding
    private var recordId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recordId = intent.getLongExtra("recordId", -1L)
        
        binding.btnCancel.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("recordId", recordId)
            startActivity(intent)
        }

        binding.btnConfirmDelete.setOnClickListener {
            deleteRecord()
        }

        loadRecordDetails()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun loadRecordDetails() {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.recordApi.getRecord(recordId)
                Log.d("API", "HTTP Code = ${response.code()}")
                if (response.isSuccessful) {
                    val record = response.body()
                    if (record != null) {
                        displayRecordInfo(record)
                    }
                } else {
                    Log.e("API", response.errorBody()?.string() ?: "")
                    Toast.makeText(
                        this@DeleteActivity,
                        "Failed to load record",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("API", e.message ?: "")
                Toast.makeText(
                    this@DeleteActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                delay(600)
                showLoading(false)
            }
        }
    }

    private fun displayRecordInfo(record: WellnessRecordResponse) {
        binding.txtRecordCategory.text = record.category?.uppercase() ?: "Unknown"
        binding.txtRecordValue.text = "${record.value} ${record.unit ?: ""}"
        val rawDate = "${record.recordDate[0]}-${record.recordDate[1]}-${record.recordDate[2]}"
        binding.txtRecordDate.text = rawDate
        binding.txtRecordNotes.text = if (record.notes.isNullOrEmpty()) "No notes" else record.notes

        binding.imageView.setImageResource(0)
        when (record.category?.lowercase()) {
            "sleep" -> binding.imageView.setImageResource(R.drawable.app_img_sleep)
            "water" -> binding.imageView.setImageResource(R.drawable.app_img_water)
            "steps" -> binding.imageView.setImageResource(R.drawable.app_img_steps)
            "exercise" -> binding.imageView.setImageResource(R.drawable.app_img_exercise)
            "mood" -> binding.imageView.setImageResource(R.drawable.app_img_mood)
            else -> binding.imageView.setImageResource(R.drawable.app_img_wellness)
        }
    }

    private fun deleteRecord() {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.recordApi.deleteRecord(recordId)

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@DeleteActivity,
                        "Record deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    delay(600)
                    val intent = Intent(this@DeleteActivity, ListViewActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@DeleteActivity,
                        "Delete failed: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@DeleteActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                delay(500)
                showLoading(false)
            }
        }
    }
}
