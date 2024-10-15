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

    fun showInAppMessage(title: String, message: String) {
        titleView?.text = title
        messageView?.text = message
        messageLayout?.visibility = View.VISIBLE

        // Hide the message after a delay
        messageLayout?.postDelayed({
            messageLayout?.visibility = View.GONE
        }, 3000) // Adjust the duration as needed
    }
}
