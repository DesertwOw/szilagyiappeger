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
import hu.radosdev.szilagyiapp.MainActivity
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.hilt.ActivityTracker
import hu.radosdev.szilagyiapp.notifications.NotificationItem
import hu.radosdev.szilagyiapp.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SzilagyiFirebaseMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveTokenToPreferences(token)
    }

    private fun saveTokenToPreferences(token: String) {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(Constants.PREFS_KEY_TOKEN, token)
            apply()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        CoroutineScope(Dispatchers.IO).launch {
            handleIncomingMessage(remoteMessage)
        }
    }

    private suspend fun handleIncomingMessage(remoteMessage: RemoteMessage) {
        val messageBody = remoteMessage.notification?.body
        val channelId = remoteMessage.notification?.channelId

        if (messageBody != null) {
            val notificationItem = NotificationItem(
                id = System.currentTimeMillis().toInt(),
                icon = R.drawable.baseline_notifications_24,
                title = messageBody,
                timestamp = "Just now",
                url = if (channelId == Constants.NEWS_CHANNEL_ID) {
                    Constants.NEWS_URL
                } else {
                    Constants.CHAT_URL
                }
            )

            if (isAppInForeground()) {
                showInAppNotification(notificationItem, channelId)
            } else {
                sendNotification(notificationItem, channelId)
            }
        }
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        val packageName = applicationContext.packageName

        return appProcesses?.any {
            it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        } == true
    }

    private fun sendNotification(notificationItem: NotificationItem, channelId: String?) {
        // Determine the URL based on the channel ID
        val url = when (channelId) {
            Constants.NEWS_CHANNEL_ID -> Constants.NEWS_URL
            Constants.CHAT_CHANNEL_ID -> Constants.CHAT_URL
            else -> notificationItem.url ?: Constants.BASE_URL // Fallback URL
        }

        Log.d("NotificationDebug", "Channel ID: $channelId, Navigating to URL: $url")

        // Create an intent for WebViewActivity with the specified URL
        val intent = Intent(this, WebViewActivity::class.java).apply {
            putExtra(Constants.URL, url) // Pass the determined URL to WebViewActivity
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Create a PendingIntent for the notification click action
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId ?: Constants.NEWS_CHANNEL_ID)
            .setSmallIcon(notificationItem.icon)
            .setContentTitle(notificationItem.title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // Set PendingIntent to open WebViewActivity

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        // Check if permission to post notifications is granted (for Android 13+)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Post the notification with a unique ID
            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }





    private suspend fun showInAppNotification(notificationItem: NotificationItem, channelId: String?) {
        withContext(Dispatchers.Main) {
            val currentActivity = getCurrentActivity() ?: return@withContext

            val inflater = LayoutInflater.from(currentActivity)
            val notificationView = inflater.inflate(R.layout.in_app_notification, null)

            val iconView = notificationView.findViewById<ImageView>(R.id.notification_icon)
            val titleView = notificationView.findViewById<TextView>(R.id.notification_title)
            val timestampView = notificationView.findViewById<TextView>(R.id.notification_timestamp)
            val actionButton = notificationView.findViewById<Button>(R.id.notification_action_button)

            iconView.setImageResource(notificationItem.icon)
            titleView.text = notificationItem.title
            timestampView.text = notificationItem.timestamp

            actionButton.setOnClickListener {
                val url = when (channelId) {
                    Constants.NEWS_CHANNEL_ID -> Constants.NEWS_URL
                    Constants.CHAT_CHANNEL_ID -> Constants.CHAT_URL
                    else -> null
                }
                url?.let {
                    val intent = Intent(currentActivity, WebViewActivity::class.java).apply {
                        putExtra(Constants.URL, it)
                    }
                    currentActivity.startActivity(intent)
                }
            }

            val overlayContainer = FrameLayout(currentActivity)
            overlayContainer.addView(notificationView)

            currentActivity.window.addContentView(overlayContainer, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ))

            notificationView.visibility = View.VISIBLE

            notificationView.postDelayed({
                notificationView.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        overlayContainer.removeView(notificationView)
                    }
                    .start()
            }, 5000)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val newsChannel = NotificationChannel(
                Constants.NEWS_CHANNEL_ID,
                Constants.NEWS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = Constants.NEWS_CHANNEL_DESCRIPTION
            }

            val chatChannel = NotificationChannel(
                Constants.CHAT_CHANNEL_ID,
                Constants.CHAT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = Constants.CHAT_CHANNEL_DESCRIPTION
            }

            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(newsChannel)
            notificationManager.createNotificationChannel(chatChannel)
        }
    }

    private fun getCurrentActivity(): AppCompatActivity? {
        val activity = ActivityTracker.getCurrentActivity()
        return activity as? AppCompatActivity
    }
}
