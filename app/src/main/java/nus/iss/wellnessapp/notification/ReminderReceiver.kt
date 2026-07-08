package nus.iss.wellnessapp.notification
// Tan Pang Wee
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("PWT", "ReminderReceiver fired")
        val type = intent.getStringExtra("TYPE") ?: "Steps"

        val title = "$type Reminder"
        val message = when (type) {
            "Steps" -> "Time for a short walk!"
            "Sleep" -> "Time to get enough rest."
            "Water" -> "Drink some water."
            else -> "Take care of your wellness."
        }

        NotificationHelper.showNotification(
            context,
            System.currentTimeMillis().toInt(),
            title,
            message
        )
//        Log.d("PWT", "ReminderReceiver showNotification done")
    }
}