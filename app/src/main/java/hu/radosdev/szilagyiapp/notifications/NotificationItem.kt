package hu.radosdev.szilagyiapp.notifications

data class NotificationItem(
    val id: Int,
    val icon: Int,
    val title: String,
    val body: String? = null,            // Optional field for notification body
    val timestamp: String,
    val url: String? = null,
    val data: Map<String, String>? = null // Map to hold any custom data payload
)
