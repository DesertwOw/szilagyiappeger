package hu.radosdev.szilagyiapp.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.fcm.InAppMessageManager
import hu.radosdev.szilagyiapp.data.fcm.WebViewActivity
import hu.radosdev.szilagyiapp.data.sharedPreferences.PreferencesManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val notificationsList = mutableListOf<NotificationItem>()
    private lateinit var notificationsAdapter: NotificationsAdapter
    private lateinit var inAppMessageManager: InAppMessageManager

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getIntExtra("id", -1) ?: return
            val iconResId = intent.getIntExtra("iconResId", R.drawable.baseline_notifications_24)
            val title = intent.getStringExtra("title") ?: "New Notification"
            val timestamp = intent.getStringExtra("timestamp") ?: "Just now"
            val url = intent.getStringExtra("url")

            addNotification(id, iconResId, title, timestamp, url)
            inAppMessageManager.showInAppMessage(title, "Received at: $timestamp") // Show in-app notification
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.notif_recycler_view)
        notificationsAdapter = NotificationsAdapter(notificationsList) { notification ->
            notification.url?.let { url ->
                startActivity(Intent(this, WebViewActivity::class.java).apply {
                    putExtra("url", url)
                })
            }
        }
        recyclerView.adapter = notificationsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize InAppMessageManager
        inAppMessageManager = InAppMessageManager()
        inAppMessageManager.initialize(findViewById(R.id.in_app_notification_layout))

        loadNotifications()

        // Register receiver for new notifications
        LocalBroadcastManager.getInstance(this).registerReceiver(
            notificationReceiver, IntentFilter("hu.radosdev.szilagyiapp.NEW_NOTIFICATION")
        )

        // Setup swipe to delete functionality
        setupSwipeToDelete(recyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver)
    }

    private fun loadNotifications() {
        lifecycleScope.launch {
            val notifications = preferencesManager.getNotifications()
            notificationsList.clear()
            notificationsList.addAll(notifications)
            notificationsAdapter.notifyDataSetChanged()
            Log.d("NotificationActivity", "Loaded notifications: $notifications")
        }
    }

    private fun addNotification(id: Int, iconResId: Int, title: String, timestamp: String, url: String?) {
        val notificationItem = NotificationItem(id, iconResId, title, timestamp, url)
        notificationsList.add(notificationItem)
        notificationsAdapter.notifyItemInserted(notificationsList.size - 1)
    }

    private fun setupSwipeToDelete(recyclerView: RecyclerView) {
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // No move operation
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val notificationItem = notificationsList[position]

                // Remove the notification from the list
                notificationsList.removeAt(position)
                notificationsAdapter.notifyItemRemoved(position)

                // Save the updated notification list to preferences
                lifecycleScope.launch {
                    preferencesManager.saveNotifications(notificationsList)
                }
            }
        }

        // Attach the ItemTouchHelper to the RecyclerView
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView)
    }
}
