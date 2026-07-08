package nus.iss.wellnessapp.notification
// Tan Pang Wee
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import java.util.Calendar

object ReminderScheduler {

    fun scheduleStepsReminder(context: Context, interval: Int) {
        Log.d("PWT", "scheduleStepsReminder  ${interval}")
        val stepsRequest =
            PeriodicWorkRequestBuilder<ReminderWorker>(
                interval.toLong(),
                TimeUnit.MINUTES
            )
                .setInputData(
                    workDataOf("TYPE" to "Steps")
                )
                .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "steps_reminder",
                ExistingPeriodicWorkPolicy.UPDATE,
                stepsRequest
            )
    }

    fun scheduleOneTimeStepsReminder(context: Context) {
        Log.d("PWT", "scheduleOneTimeStepsReminder SCHEDULE CALLED")
        val waterRequest =
            OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(30, TimeUnit.SECONDS)
                .setInputData(
                    workDataOf("TYPE" to "Water")
                )
                .build()

        val moodRequest =
            OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(45, TimeUnit.SECONDS)
                .setInputData(
                    workDataOf("TYPE" to "Mood")
                )
                .build()

        val exerciseRequest =
            OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(60, TimeUnit.SECONDS)
                .setInputData(
                    workDataOf("TYPE" to "Exercise")
                )
                .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(waterRequest)
        workManager.enqueue(moodRequest)
        workManager.enqueue(exerciseRequest)
        Log.d("PWT", "work enqueue")
    }

    fun scheduleReminderAt(
        context: Context,
        type: String,
        hour: Int,
        minute: Int
    ) {
//        Log.d("PWT", "ScheduleReminderAt Start")
        Log.d("PWT", "ScheduleReminderAt Start ${hour} ${minute}")
//        val calendar = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, hour)
//            set(Calendar.MINUTE, minute)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//
//            if (before(Calendar.getInstance())) {
//                add(Calendar.DAY_OF_MONTH, 1)
//            }
//        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

//        Log.d("PWT", "scheduleReminderAt Default TimeZone = ${java.util.TimeZone.getDefault().id}")
//        Log.d("PWT", "scheduleReminderAt Now = ${Calendar.getInstance().time}")
//        Log.d("PWT", "scheduleReminderAt Alarm = ${calendar.time}")
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TYPE", type)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            type.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//        Log.d("PWT", "Now = ${Calendar.getInstance().time}")
//        Log.d("PWT", "Alarm schedule local = ${calendar.time}")
//        Log.d("PWT", "Alarm millis = ${calendar.timeInMillis}")
//        Log.d("PWT", "alarm schedule ${calendar.time}")
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Log.d("PWT", "scheduleReminderAt done")
    }
}