package hu.radosdev.szilagyiapp.menu

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.entity.MainMenuItem
import hu.radosdev.szilagyiapp.data.entity.ChildMenuItem

class MenuAdapter(
    private var menuItems: List<MainMenuItem>,
    private val onMainMenuItemClick: (MainMenuItem) -> Unit,
    private val onChildMenuItemClick: (ChildMenuItem) -> Unit,
    private val context: Context // Add context parameter
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private var expandedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)

        // Click listener for expanding/collapsing the submenu via the icon only
        holder.expandIcon.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            val wasExpanded = expandedPosition == adapterPosition

            // Collapse previously expanded item if necessary
            if (expandedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(expandedPosition)
            }

            // Toggle expansion for the clicked item
            expandedPosition = if (wasExpanded) {
                RecyclerView.NO_POSITION  // Collapse if it was expanded
            } else {
                adapterPosition  // Expand the new item
            }

            // Notify all items to refresh their highlight state
            notifyDataSetChanged()
        }

        // Update submenu visibility based on whether the item is expanded or collapsed
        val isExpanded = holder.adapterPosition == expandedPosition
        holder.submenuRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Animate submenu visibility (expand/collapse)
        if (isExpanded) {
            holder.submenuRecyclerView.alpha = 0f
            holder.submenuRecyclerView.animate().alpha(1f).setDuration(300).start()
        }

        // Set up submenu adapter if expanded
        if (isExpanded) {
            holder.submenuRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.submenuRecyclerView.adapter = ChildMenuAdapter(menuItem.childs ?: emptyList()) { childMenuItem ->
                // Check if the child menu item has a URL and whether it includes "szilagyi"
                val url = childMenuItem.url // Assuming there's a url property in ChildMenuItem
                if (url != null && !url.contains("szilagyi")) {
                    // Open in default browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } else {
                    // Proceed with the onChildMenuItemClick action
                    onChildMenuItemClick(childMenuItem)
                }

                // Reset highlights for all main menu items when a submenu item is clicked
                expandedPosition = RecyclerView.NO_POSITION  // Collapse all
                notifyDataSetChanged()  // Refresh the entire list
            }
        }

        // Reset background color for all items and highlight the selected one
        holder.titleTextView.setBackgroundColor(
            if (holder.adapterPosition == expandedPosition) {
                // Set the background color for the expanded item
                ContextCompat.getColor(holder.itemView.context, R.color.primary_pantone_orange) // Replace with your highlight color
            } else {
                // Reset to default background color
                Color.TRANSPARENT // or your default background color
            }
        )

        // Change the background of the expandIcon based on expanded state
        holder.expandIcon.setBackgroundColor(
            if (holder.adapterPosition == expandedPosition) {
                // Set the background color when expanded
                ContextCompat.getColor(holder.itemView.context, R.color.primary_pantone_orange) // Use the same or a different color for the icon
            } else {
                // Reset the background color when collapsed
                Color.TRANSPARENT
            }
        )
    }

    fun updateMenu(items: List<MainMenuItem>) {
        menuItems = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.menu_item_title)
        val expandIcon: ImageView = itemView.findViewById(R.id.menu_item_expand_icon)
        val submenuRecyclerView: RecyclerView = itemView.findViewById(R.id.submenu_recycler_view)

        fun bind(mainMenuItem: MainMenuItem) {
            titleTextView.text = mainMenuItem.title

            // Show the expand/collapse icon only if there are child items
            if (mainMenuItem.childs != null && mainMenuItem.childs.isNotEmpty()) {
                expandIcon.visibility = View.VISIBLE
                val isExpanded = adapterPosition == expandedPosition

                // Rotate icon based on expanded/collapsed state
                expandIcon.rotation = if (isExpanded) 180f else 0f
                expandIcon.animate().rotation(if (isExpanded) 180f else 0f).setDuration(300).start()
            } else {
                expandIcon.visibility = View.GONE
            }
        }
    }
}
