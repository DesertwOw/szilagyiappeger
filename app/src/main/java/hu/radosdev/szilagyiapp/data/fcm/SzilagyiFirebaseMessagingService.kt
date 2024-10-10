package hu.radosdev.szilagyiapp.data.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SzilagyiFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Use a coroutine to handle the notification processing
        CoroutineScope(Dispatchers.IO).launch {
            handleIncomingMessage(remoteMessage)
        }
    }

    private fun handleIncomingMessage(remoteMessage: RemoteMessage) {
        // Check if there's a notification body
        val messageBody = remoteMessage.notification?.body

       run {
            // If there's no URL, send a simple notification
            messageBody?.let {
                notificationHelper.sendNotification(messageBody)
                Log.d("FCM", "Received message: $messageBody without URL")
            }
        }
    }
}
