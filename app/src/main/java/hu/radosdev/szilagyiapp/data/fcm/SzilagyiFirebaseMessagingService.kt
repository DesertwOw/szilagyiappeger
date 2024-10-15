package hu.radosdev.szilagyiapp.data.fcm

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.hilt.ActivityTracker
import hu.radosdev.szilagyiapp.data.sharedPreferences.PreferencesManager
import hu.radosdev.szilagyiapp.notifications.NotificationItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SzilagyiFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    companion object {
        private const val CHANNEL_ID = "my_channel_id"
        private const val CHANNEL_NAME = "Your Channel Name"
        private const val CHANNEL_DESCRIPTION = "Channel Description"
        private const val TAG = "SzilagyiFirebaseService"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        CoroutineScope(Dispatchers.IO).launch {
            handleIncomingMessage(remoteMessage)
        }
    }

    private suspend fun handleIncomingMessage(remoteMessage: RemoteMessage) {
        val messageBody = remoteMessage.notification?.body
        val url = remoteMessage.data["url"]

        if (messageBody != null) {
            val notificationItem = NotificationItem(
                id = System.currentTimeMillis().toInt(),
                icon = R.drawable.baseline_notifications_24,
                title = messageBody,
                timestamp = "Just now",
                url = url
            )

            Log.d(TAG, "Notification item created: $notificationItem")

            // Save the notification
            saveNotification(notificationItem)

            if (isAppInForeground()) {
                Log.d(TAG, "App is in foreground, showing in-app notification.")
                showInAppNotification(notificationItem)
            } else {
                Log.d(TAG, "App is in background, sending notification.")
                sendNotification(notificationItem)
            }
        }
    }

    private fun saveNotification(notificationItem: NotificationItem) {
        val currentNotifications = preferencesManager.getNotifications().toMutableList()
        currentNotifications.add(notificationItem)
        preferencesManager.saveNotifications(currentNotifications)
        Log.d(TAG, "Notification saved: $notificationItem")
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        val packageName = applicationContext.packageName

        val isForeground = appProcesses?.any {
            it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        } == true

        Log.d(TAG, "App is in foreground: $isForeground")
        return isForeground
    }

    private fun sendNotification(notificationItem: NotificationItem) {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(notificationItem.icon)
            .setContentTitle("Notification")
            .setContentText(notificationItem.title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Set click action if URL is provided
        notificationItem.url?.let { url ->
            val intent = Intent(this, WebViewActivity::class.java).apply {
                putExtra("url", url)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            notificationBuilder.setContentIntent(pendingIntent)
        }

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Handle permission request
            Log.w(TAG, "Notification permission not granted")
            return
        }

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
        Log.d(TAG, "Notification sent with ID: $notificationId")
    }

    private suspend fun showInAppNotification(notificationItem: NotificationItem) {
        withContext(Dispatchers.Main) {
            val currentActivity = getCurrentActivity() ?: return@withContext

            // Inflate the in-app notification layout
            val inflater = LayoutInflater.from(currentActivity)
            val notificationView = inflater.inflate(R.layout.in_app_notification, null)

            // Set notification details
            val iconView = notificationView.findViewById<ImageView>(R.id.notification_icon)
            val titleView = notificationView.findViewById<TextView>(R.id.notification_title)
            val timestampView = notificationView.findViewById<TextView>(R.id.notification_timestamp)
            val actionButton = notificationView.findViewById<Button>(R.id.notification_action_button)

            iconView.setImageResource(notificationItem.icon)
            titleView.text = notificationItem.title
            timestampView.text = notificationItem.timestamp

            // Set the action button's click listener
            actionButton.setOnClickListener {
                notificationItem.url?.let { url ->
                    val intent = Intent(currentActivity, WebViewActivity::class.java).apply {
                        putExtra("url", url)
                    }
                    currentActivity.startActivity(intent)
                }
            }

            // Create a FrameLayout as the overlay container
            val overlayContainer = FrameLayout(currentActivity)
            overlayContainer.addView(notificationView)

            // Add the overlay to the current activity's window
            currentActivity.window.addContentView(overlayContainer, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ))

            // Show the notification view
            notificationView.visibility = View.VISIBLE

            // Dismiss the notification after a delay
            notificationView.postDelayed({
                notificationView.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        overlayContainer.removeView(notificationView)
                    }
                    .start()
            }, 5000) // Auto-dismiss after 5 seconds
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created.")
        }
    }

    private fun getCurrentActivity(): AppCompatActivity? {
        val activity = ActivityTracker.getCurrentActivity()
        Log.d(TAG, "Current activity: $activity")
        return activity as? AppCompatActivity
    }
}
