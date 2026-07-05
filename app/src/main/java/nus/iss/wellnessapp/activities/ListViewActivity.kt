package nus.iss.wellnessapp.activities

import android.content.Intent
import androidx.activity.enableEdgeToEdge
import nus.iss.wellnessapp.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import nus.iss.wellnessapp.adapter.CustomAdapter
import nus.iss.wellnessapp.api.RetrofitClient
import nus.iss.wellnessapp.model.WellnessRecordResponse

class ListViewActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private var lastView: View? = null  // track last selected item

    private var records: List<WellnessRecordResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_view)
        supportActionBar?.apply {
            title = "Select for edit"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listView = findViewById<ListView>(R.id.listView)
        listView?.adapter = CustomAdapter(this, emptyList())
        listView?.setOnItemClickListener(this)

        loadRecords()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
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
            try {

                val response = RetrofitClient.recordApi.getRecordsByUserId(1L)

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
            }
        }
    }
}