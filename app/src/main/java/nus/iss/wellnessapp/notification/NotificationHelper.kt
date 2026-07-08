package nus.iss.wellnessapp.notification
// Tan Pang Wee
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.activities.DashboardActivity

object NotificationHelper {

    const val CHANNEL_ID = "wellness_channel"

    /**
     * Create notification channel (Android 8.0+)
     * Call once when the app starts.
     */
    fun createNotificationChannel(context: Context) {

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Wellness Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Daily wellness reminder notifications"
        }

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        manager.createNotificationChannel(channel)

        Log.d("PWT", "Notification created")
        Log.d("PWT", CHANNEL_ID)
    }

    /**
     * Show notification
     */
    fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String
    ) {

        val intent = Intent(context, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

        Log.d("PWT", "NotificationHelper Before Permission")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("PWT", "NotificationHelper Permission denied")
            return
        }
        Log.d("PWT", "NotificationHelper Permission granted")
        NotificationManagerCompat.from(context)
            .notify(notificationId, builder.build())
        Log.d("PWT", "NotificationHelper DONE")
    }
}