package nus.iss.wellnessapp.activities
// author : Tan Pang Wee
import android.app.ProgressDialog.show
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import nus.iss.wellnessapp.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nus.iss.wellnessapp.adapter.CustomAdapter
import nus.iss.wellnessapp.api.RetrofitClient
import nus.iss.wellnessapp.model.WellnessRecordResponse
import nus.iss.wellnessapp.storage.TokenManager

class ListViewActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private var lastView: View? = null  // track last selected item

    private var records: List<WellnessRecordResponse> = emptyList()

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
            val popup = PopupMenu(this, btnFilter)

            popup.menu.add("All")
            popup.menu.add("Steps")
            popup.menu.add("Water")
            popup.menu.add("Sleep")
            popup.menu.add("Mood")
            popup.menu.add("Exercise")

            popup.setOnMenuItemClickListener { item ->
                filterByCategory(item.title.toString())
                true
            }

            popup.show()
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

//              val response = RetrofitClient.recordApi.getRecordsByUserId(1L)
                val response = RetrofitClient.recordApi.getRecordsByUserId(TokenManager.getUserId())

                if (response.isSuccessful) {
                    records = response.body() ?: emptyList()

                    val listView = findViewById<ListView>(R.id.listView)
                    listView.adapter = CustomAdapter(this@ListViewActivity, records)
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
}