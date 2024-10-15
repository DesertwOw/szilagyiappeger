    package hu.radosdev.szilagyiapp.notifications

    data class NotificationItem(
        val id: Int,
        val icon: Int,
        val title: String,
        val timestamp: String,
        val url: String? = null
    )
