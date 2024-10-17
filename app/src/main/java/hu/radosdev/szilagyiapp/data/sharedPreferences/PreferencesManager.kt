package hu.radosdev.szilagyiapp.data.sharedPreferences

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.radosdev.szilagyiapp.notifications.NotificationItem
import javax.inject.Inject

class PreferencesManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun saveNotification(notificationItem: NotificationItem) {
        val notificationsJson = getNotifications().toMutableList()
        notificationsJson.add(notificationItem)

        // Convert the list to JSON and save it in SharedPreferences
        val jsonString = Gson().toJson(notificationsJson)
        sharedPreferences.edit().putString("notifications", jsonString).apply()
        Log.d("PreferencesManager", "Notification saved: $notificationItem")
    }

    fun getNotifications(): List<NotificationItem> {
        val jsonString = sharedPreferences.getString("notifications", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<NotificationItem>>() {}.type
            Gson().fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }

    fun saveNotifications(notifications: List<NotificationItem>) {
        val jsonString = Gson().toJson(notifications)
        sharedPreferences.edit().putString("notifications", jsonString).apply()
        Log.d("PreferencesManager", "Notifications list saved.")
    }
}
