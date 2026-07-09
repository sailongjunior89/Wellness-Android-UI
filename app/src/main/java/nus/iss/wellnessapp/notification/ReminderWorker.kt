package nus.iss.wellnessapp.notification
// Author : Tan Pang Wee
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        Log.d("PWT", "ReminderWorker fire")

        val type = inputData.getString("TYPE") ?: "Steps"

        val (title, message) = when (type) {

            "Steps" -> Pair(
                "🚶 Steps Reminder",
                "Time for a short walk. Keep moving!"
            )

            "Sleep" -> Pair(
                "😴 Sleep Reminder",
                "It's bedtime. Remember to get enough rest."
            )

            "Water" -> Pair(
                "Drink water reminder",
                "Your body need to be hydrated."
            )

            "Mood" -> Pair(
                "Watch your mood.",
                "Mood affects your health."
            )

            "Exercise" -> Pair(
                "Exercise Reminder.",
                "Exercise reduce stress."
            )

            else -> Pair(
                "Wellness Reminder",
                "Take care of your health today."
            )
        }

        NotificationHelper.showNotification(
            applicationContext,
            System.currentTimeMillis().toInt(),
            title,
            message
        )

        return Result.success()
    }
}