package nus.iss.wellnessapp.activities
// Author: Si Hua
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import nus.iss.wellnessapp.R
import kotlin.jvm.java

class CategoryPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_picker)

        // Map each button to its backend category name (must match the enum: sleep, exercise, mood, water, steps)
        val buttons = mapOf(
            R.id.btnSleep to "sleep",
            R.id.btnExercise to "exercise",
            R.id.btnMood to "mood",
            R.id.btnWater to "water",
            R.id.btnSteps to "steps"
        )

        buttons.forEach { (buttonId, category) ->
            findViewById<Button>(buttonId).setOnClickListener {
                val intent = Intent(this, LogCategoryActivity::class.java)
                intent.putExtra("category", category)   // tells the next screen which category it is
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }
}