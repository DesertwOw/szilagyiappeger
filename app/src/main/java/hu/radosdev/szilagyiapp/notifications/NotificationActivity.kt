package hu.radosdev.szilagyiapp.notifications

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessaging
import hu.radosdev.szilagyiapp.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationsAdapter: NotificationsAdapter
    private val notificationsList = mutableListOf<NotificationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val recyclerView: RecyclerView = findViewById(R.id.notif_recycler_view)
        notificationsAdapter = NotificationsAdapter(notificationsList)
        recyclerView.adapter = notificationsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            fetchFcmToken()
        }

        // Swipe to delete
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                notificationsAdapter.removeItem(position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val background = ColorDrawable(Color.RED)
                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Handle notifications (this is an example)
        FirebaseMessaging.getInstance().subscribeToTopic("general")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Get token and handle it
                val token = task.result
                // Use token for backend handling (if needed)
            }
        }
    }

    // Example method to add a new notification
    fun addNotification(iconResId: Int, title: String, timestamp: String) {
        notificationsList.add(NotificationItem(iconResId, title, timestamp))
        notificationsAdapter.notifyItemInserted(notificationsList.size - 1)
    }

    private suspend fun fetchFcmToken() {
        val token = FirebaseMessaging.getInstance().token.await()
        Log.d("FCM", "FCM Token: $token")
        // Optionally, handle the token
    }
}