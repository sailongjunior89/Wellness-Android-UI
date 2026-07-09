package nus.iss.wellnessapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nus.iss.wellnessapp.R

import android.app.TimePickerDialog
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Toast
import nus.iss.wellnessapp.notification.ReminderScheduler
class NotificationActivity : AppCompatActivity() {

    private var sleepHour = 22
    private var sleepMinute = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val stepsSpinner = findViewById<android.widget.Spinner>(R.id.spStepsInterval)
        val waterSpinner = findViewById<android.widget.Spinner>(R.id.spWaterInterval)
        val moodSpinner = findViewById<android.widget.Spinner>(R.id.spMoodInterval)
        val btnSleepTime = findViewById<android.widget.Button>(R.id.btnSleepTime)
        val txtSleepTime = findViewById<android.widget.TextView>(R.id.txtSleepTime)
        val btnSave = findViewById<android.widget.Button>(R.id.btnSaveNotification)
        val btnBack = findViewById<android.widget.Button>(R.id.btnBack)

        val labels = listOf("15 min", "30 min", "60 min", "120 min")
        val values = listOf(15, 30, 60, 120)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)

        stepsSpinner.adapter = adapter
        waterSpinner.adapter = adapter
        moodSpinner.adapter = adapter

        val prefs = getSharedPreferences("notification_settings", MODE_PRIVATE)

        val stepsInterval = prefs.getInt("steps_interval", 30)
        val waterInterval = prefs.getInt("water_interval", 60)
        val moodInterval = prefs.getInt("mood_interval", 120)

        stepsSpinner.setSelection(values.indexOf(stepsInterval))
        waterSpinner.setSelection(values.indexOf(waterInterval))
        moodSpinner.setSelection(values.indexOf(moodInterval))

        sleepHour = prefs.getInt("sleep_hour", 22)
        sleepMinute = prefs.getInt("sleep_minute", 30)
        txtSleepTime.text = "Sleep Time: %02d:%02d".format(sleepHour, sleepMinute)

        btnSleepTime.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    sleepHour = hour
                    sleepMinute = minute
                    txtSleepTime.text = "Sleep Time: %02d:%02d".format(hour, minute)
                },
                sleepHour,
                sleepMinute,
                true
            ).show()
        }

        btnSave.setOnClickListener {
            val stepsInterval = values[stepsSpinner.selectedItemPosition]
            val waterInterval = values[waterSpinner.selectedItemPosition]
            val moodInterval = values[moodSpinner.selectedItemPosition]

            prefs.edit()
                .putInt("steps_interval", stepsInterval)
                .putInt("water_interval", waterInterval)
                .putInt("mood_interval", moodInterval)
                .putInt("sleep_hour", sleepHour)
                .putInt("sleep_minute", sleepMinute)
                .apply()

            ReminderScheduler.scheduleStepsReminder(this@NotificationActivity, stepsInterval)
//            ReminderScheduler.scheduleStepsReminder(this@NotificationActivity)
//            ReminderScheduler.scheduleWaterReminder(this, waterInterval.toLong())
//            ReminderScheduler.scheduleMoodReminder(this, moodInterval.toLong())
            ReminderScheduler.scheduleReminderAt(this, "Sleep", sleepHour, sleepMinute)

            Toast.makeText(this, "Notification settings saved", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}