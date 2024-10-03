package hu.radosdev.szilagyiapp.data.fcm
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class SzilagyiFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Ellenőrzés, hogy van-e értesítési test
        val messageBody = remoteMessage.notification?.body

        // Ellenőrizzük, hogy van-e URL az adat mezőben
        remoteMessage.data["url"]?.let { url ->
            notificationHelper.sendNotificationWithUrl(messageBody ?: "Default message", url)
            Log.d("FCM", "Received message: $messageBody with URL: ${remoteMessage.data["url"]}")

        }?: run {
            // Ha nincs URL, egyszerű értesítést küldünk
            messageBody?.let {
                notificationHelper.sendNotification(messageBody)
                Log.d("FCM", "Received message: $messageBody with URL: ${remoteMessage.data["url"]}")

            }
        }
    }
}
