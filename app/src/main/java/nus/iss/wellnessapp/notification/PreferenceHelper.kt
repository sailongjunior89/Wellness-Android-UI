package nus.iss.wellnessapp.notification

import android.content.Context

object PreferenceHelper {

    fun initialize(context: Context) {

        val prefs = context.getSharedPreferences(
            "notification_settings",
            Context.MODE_PRIVATE
        )

        // Already initialized
        if (prefs.contains("steps_interval")) {
            return
        }

        prefs.edit()
            .putInt("steps_interval", 15)
            .putInt("water_interval", 60)
            .putInt("mood_interval", 120)
            .putInt("sleep_hour", 12)
            .putInt("sleep_minute", 30)
            .apply()
    }

    fun getStepsInterval(context: Context): Int {
        val prefs = context.getSharedPreferences(
            "notification_settings",
            Context.MODE_PRIVATE
        )

        return prefs.getInt("steps_interval", 30)
    }

    fun getWaterInterval(context: Context): Int {
        val prefs = context.getSharedPreferences(
            "notification_settings",
            Context.MODE_PRIVATE
        )

        return prefs.getInt("water_interval", 60)
    }

    fun getMoodInterval(context: Context): Int {
        val prefs = context.getSharedPreferences(
            "notification_settings",
            Context.MODE_PRIVATE
        )

        return prefs.getInt("mood_interval", 120)
    }

    fun getSleepHour(context: Context): Int {
        val prefs = context.getSharedPreferences(
            "notification_settings",
            Context.MODE_PRIVATE
        )

        return prefs.getInt("sleep_hour", 22)
    }

    fun getSleepMinute(context: Context): Int {
        val prefs = context.getSharedPreferences(
            "notification_settings",
            Context.MODE_PRIVATE
        )

        return prefs.getInt("sleep_minute", 30)
    }

    fun isReminderScheduled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(
            "notification_settings",
            Context.MODE_PRIVATE
        )
        return prefs.getBoolean("reminder_scheduled", false)
    }

    fun setReminderScheduled(context: Context, scheduled: Boolean) {
        val prefs = context.getSharedPreferences(
            "notification_settings",
            Context.MODE_PRIVATE
        )

        prefs.edit()
            .putBoolean("reminder_scheduled", scheduled)
            .apply()
    }
}

