package hu.radosdev.szilagyiapp.data.fcm

import android.view.View
import android.widget.TextView
import hu.radosdev.szilagyiapp.R

class InAppMessageManager {

    private var messageLayout: View? = null
    private var titleView: TextView? = null
    private var messageView: TextView? = null

    fun initialize(messageLayout: View) {
        this.messageLayout = messageLayout
        titleView = messageLayout.findViewById(R.id.in_app_notification_title)
        messageView = messageLayout.findViewById(R.id.in_app_notification_message)
    }

}
