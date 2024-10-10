package hu.radosdev.szilagyiapp.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.radosdev.szilagyiapp.R

class NotificationsAdapter(
    private var notificationList: MutableList<NotificationItem>
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.notification_icon)
        val title: TextView = itemView.findViewById(R.id.notification_title)
        val timestamp: TextView = itemView.findViewById(R.id.notification_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.icon.setImageResource(notification.icon)
        holder.title.text = notification.title
        holder.timestamp.text = notification.timestamp
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    fun removeItem(position: Int) {
        notificationList.removeAt(position)
        notifyItemRemoved(position)
    }
}
