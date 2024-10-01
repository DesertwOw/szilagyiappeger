package hu.radosdev.szilagyiapp.data.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class SzilagyiFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper : NotificationHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.notification?.body?.let { messageBody ->
            notificationHelper.sendNotification(messageBody)
        }
    }
}