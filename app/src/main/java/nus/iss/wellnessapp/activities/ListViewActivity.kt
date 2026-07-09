package nus.iss.wellnessapp.activities
// author : Tan Pang Wee
import android.app.DatePickerDialog
import android.app.ProgressDialog.show
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import nus.iss.wellnessapp.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nus.iss.wellnessapp.adapter.CustomAdapter
import nus.iss.wellnessapp.api.RetrofitClient
import nus.iss.wellnessapp.model.WellnessRecordResponse
import nus.iss.wellnessapp.model.dateInt
import nus.iss.wellnessapp.storage.TokenManager
import java.util.Calendar

class ListViewActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private var lastView: View? = null  // track last selected item

    private var records: List<WellnessRecordResponse> = emptyList()
    private var allRecords: List<WellnessRecordResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_view)

        val buttonBack = findViewById<FrameLayout>(R.id.btnBack)
        buttonBack.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
//        val buttonFilter = findViewById<FrameLayout>(R.id.btnFilter)
//        buttonFilter.setOnClickListener {
//            Toast.makeText(this, "filter", Toast.LENGTH_SHORT).show()
//        }
        val listView = findViewById<ListView>(R.id.listView)
        listView?.adapter = CustomAdapter(this, emptyList())
        listView?.setOnItemClickListener(this)
        loadRecords()

        val btnFilter = findViewById<FrameLayout>(R.id.btnFilter)
        btnFilter.setOnClickListener {
            showFilterDialog()

//            val popup = PopupMenu(this, btnFilter)
//
//            popup.menu.add("All")
//            popup.menu.add("Steps")
//            popup.menu.add("Water")
//            popup.menu.add("Sleep")
//            popup.menu.add("Mood")
//            popup.menu.add("Exercise")
//
//            popup.setOnMenuItemClickListener { item ->
//                filterByCategory(item.title.toString())
//                true
//            }
//
//            popup.show()
        }

    }

    private fun filterByCategory(category: String) {
//        Toast.makeText(this, "${category}", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.recordApi.getRecordsByUserId(TokenManager.getUserId())
                if (response.isSuccessful) {
                    records = response.body() ?: emptyList()

//                    val listView = findViewById<ListView>(R.id.listView)
//                    if (category == "All") {
//                        listView.adapter = CustomAdapter(this@ListViewActivity, records)
//                    }
                    records = if (category == "All") {
                        records
                    } else {
                        records.filter {
                            it.category.equals(category, ignoreCase = true)
                        }
                    }

                    val listView = findViewById<ListView>(R.id.listView)
                    listView.adapter = CustomAdapter(this@ListViewActivity, records)

                    val tvNoRecord = findViewById<TextView>(R.id.txtViewNoRecords)
                    if (records.size == 0 ){
                        tvNoRecord.visibility = View.VISIBLE
                    } else {
                        tvNoRecord.visibility = View.GONE
                    }

                } else {
                    Log.e("PWT", response.errorBody()?.string() ?: "No error body")
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@ListViewActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                delay(300)
                showLoading(false)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        val overLayProgressBar = findViewById<FrameLayout>(R.id.loadingOverlay)
        overLayProgressBar.visibility =
            if (show) View.VISIBLE else View.GONE
    }

    override fun onItemClick(av: AdapterView<*>?, v: View, pos: Int, id: Long) {
        val selectedRecord = records[pos]

        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("recordId", selectedRecord.id)
        startActivity(intent)

        // remember last selected item
        lastView = v
    }

    private fun loadRecords() {

        lifecycleScope.launch {
            showLoading(true)
            try {

                val response = RetrofitClient.recordApi.getRecordsByUserId(TokenManager.getUserId())

                if (response.isSuccessful) {
                    allRecords = response.body() ?: emptyList()
                    records = allRecords
                    val listView = findViewById<ListView>(R.id.listView)
                    listView.adapter = CustomAdapter(this@ListViewActivity, records)
                    val tvNoRecord = findViewById<TextView>(R.id.txtViewNoRecords)
                    if (records.size == 0 ){
                        tvNoRecord.visibility = View.VISIBLE
                    } else {
                        tvNoRecord.visibility = View.GONE
                    }

                } else {
                    Log.e("PWT", response.errorBody()?.string() ?: "No error body")
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@ListViewActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                delay(300)
                showLoading(false)
            }
        }
    }

    private fun showFilterDialog() {

        val view = layoutInflater.inflate(R.layout.dialog_filter, null)

        val spCategory = view.findViewById<Spinner>(R.id.spCategory)
        val etStartDate = view.findViewById<EditText>(R.id.etStartDate)
        val etEndDate = view.findViewById<EditText>(R.id.etEndDate)

        val categories = listOf("All", "Steps", "Water", "Sleep", "Mood", "Exercise")
        spCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        etStartDate.setOnClickListener {
            showDatePicker(etStartDate)
        }

        etEndDate.setOnClickListener {
            showDatePicker(etEndDate)
        }

//        AlertDialog.Builder(this)
//            .setTitle("Filter Records")
//            .setView(view)
//            .setPositiveButton("Apply") { _, _ ->
//                val category = spCategory.selectedItem.toString()
//                val startDate = etStartDate.text.toString()
//                val endDate = etEndDate.text.toString()
//
//                filterRecords(category, startDate, endDate)
//            }
//            .setNegativeButton("Reset") { _, _ ->
//                spCategory.setSelection(0)      // "All"
//                etStartDate.setText("")
//                etEndDate.setText("")
//            }
//            .show()

        val dialog = AlertDialog.Builder(this)
            .setTitle("Filter Records")
            .setView(view)
            .setPositiveButton("Apply") { _, _ ->
                val category = spCategory.selectedItem.toString()
                val startDate = etStartDate.text.toString()
                val endDate = etEndDate.text.toString()

                filterRecords(category, startDate, endDate)
            }
            .setNeutralButton("Reset", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {

            spCategory.setSelection(0)
            etStartDate.setText("")
            etEndDate.setText("")

            records = allRecords
            findViewById<ListView>(R.id.listView).adapter =
                CustomAdapter(this@ListViewActivity, records)

            // Dialog stays open because we don't call dialog.dismiss()
        }
    }

    private fun showDatePicker(editText: EditText) {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->

                editText.setText(
                    "%02d-%02d-%04d".format(
                        day,
                        month + 1,
                        year
                    )
                )

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun filterRecords(category: String, startDate: String, endDate: String) {
        val start = dateStringToInt(startDate)
        val end = dateStringToInt(endDate)

        val filtered = allRecords.filter { record ->
            val categoryMatch =
                category == "All" || record.category.equals(category, ignoreCase = true)

            val recordDate = record.dateInt()

            val startMatch = startDate.isBlank() || recordDate >= start
            val endMatch = endDate.isBlank() || recordDate <= end

            categoryMatch && startMatch && endMatch
        }

        val tvNoRecord = findViewById<TextView>(R.id.txtViewNoRecords)
        if (filtered.size == 0 ){
            tvNoRecord.visibility = View.VISIBLE
        } else {
            tvNoRecord.visibility = View.GONE
        }

        findViewById<ListView>(R.id.listView).adapter =
            CustomAdapter(this, filtered)

        records = filtered
    }

    private fun dateStringToInt(date: String): Int {
        if (date.isBlank()) return 0

        // expects dd-MM-yyyy
        val parts = date.split("-")

        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()

        return year * 10000 + month * 100 + day
    }
}