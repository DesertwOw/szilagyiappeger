package hu.radosdev.szilagyiapp.data.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.hilt.ActivityTracker
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

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        CoroutineScope(Dispatchers.IO).launch {
            remoteMessage.notification?.let { notification ->
                showInAppNotification(notification.body)
            }
        }
    }

    private suspend fun showInAppNotification(messageBody: String?) {
        if (messageBody != null) {
            val currentActivity = getCurrentActivity() ?: return

            val notificationView = withContext(Dispatchers.Main) {
                val inflater = LayoutInflater.from(currentActivity)
                inflater.inflate(R.layout.in_app_notification, null)
            }

            val iconView = notificationView.findViewById<ImageView>(R.id.notification_icon)
            val titleView = notificationView.findViewById<TextView>(R.id.notification_title)
            val actionButton = notificationView.findViewById<Button>(R.id.notification_action_button)

            iconView.setImageResource(R.drawable.baseline_notifications_24)
            titleView.text = messageBody

            actionButton.setOnClickListener {
                val intent = Intent(currentActivity, WebViewActivity::class.java).apply {
                    putExtra(Constants.URL, Constants.CHAT_URL)
                }
                currentActivity.startActivity(intent)
            }

            withContext(Dispatchers.Main) {
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
    }


    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                Constants.CHAT_CHANNEL_ID,
                Constants.CHAT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = Constants.CHAT_CHANNEL_DESCRIPTION
            }

            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getCurrentActivity(): AppCompatActivity? {
        val activity = ActivityTracker.getCurrentActivity()
        return activity as? AppCompatActivity
    }
}