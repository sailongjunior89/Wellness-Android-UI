package nus.iss.wellnessapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.notification.NotificationHelper
import nus.iss.wellnessapp.notification.NotificationPermissionHelper
import nus.iss.wellnessapp.notification.PreferenceHelper
import nus.iss.wellnessapp.notification.ReminderScheduler

class NotificationSetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification_setup)

        // Tan Pang Wee : Prompt Notification permission before go to dashboard
        PreferenceHelper.initialize(this)
        NotificationHelper.createNotificationChannel(this)
        NotificationPermissionHelper.requestOrRun(this) {
            scheduleRemindersAndGoDashboard()
        }
    }
    // Tan Pang Wee Notification
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        NotificationPermissionHelper.handleResult(
            this,
            requestCode,
            grantResults
        ) {
            scheduleRemindersAndGoDashboard()
        }
    }

    private fun scheduleRemindersAndGoDashboard() {
//        Log.d("PWT", "scheduleRemindersAndGoDashboard")
        if (PreferenceHelper.isReminderScheduled(this)) {
            Log.d("PWT", "Reminders already scheduled")
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
        ReminderScheduler.scheduleOneTimeStepsReminder(this)
        val stepsInterval = PreferenceHelper.getStepsInterval(this)
        val sleepHr = PreferenceHelper.getSleepHour(this)
        val sleepMin  = PreferenceHelper.getSleepMinute(this)
//        ReminderScheduler.scheduleStepsReminder(this, stepsInterval)
        ReminderScheduler.scheduleReminderAt(this, "Sleep", sleepHr, sleepMin)
        PreferenceHelper.setReminderScheduled(this, true)
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}

