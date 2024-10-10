package hu.radosdev.szilagyiapp.menu

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.entity.ChildMenuItem

class ChildMenuAdapter(
    private var childMenuItems: List<ChildMenuItem>,
    private val onChildMenuItemClick: (ChildMenuItem) -> Unit
) : RecyclerView.Adapter<ChildMenuAdapter.ChildMenuViewHolder>() {

    // Map to track expanded state for each item
    private val expandedChildStates = mutableMapOf<Int, Boolean>()

    // Variable to store the selected item position
    private var selectedPosition: Int? = null

    inner class ChildMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.child_menu_item_title)
        val submenuRecyclerView: RecyclerView = itemView.findViewById(R.id.submenu_recycler_view)
        val expandIcon: ImageView = itemView.findViewById(R.id.menu_item_expand_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.child_menu_item, parent, false)
        return ChildMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildMenuViewHolder, position: Int) {
        val childMenuItem = childMenuItems[holder.adapterPosition]
        holder.title.text = childMenuItem.title

        // Check if the child has nested sub-items (grandchildren)
        if (childMenuItem.childs?.isNotEmpty() == true) {
            // Show the expand icon when there are nested items
            holder.expandIcon.visibility = View.VISIBLE

            // Set the rotation of the arrow based on expansion state
            holder.expandIcon.rotation = if (expandedChildStates[holder.adapterPosition] == true) 180f else 0f

            // Set background color for the title based on selection and expansion state
            if (holder.adapterPosition == selectedPosition && expandedChildStates[holder.adapterPosition] == true) {
                holder.title.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_pantone_orange)) // Highlight color
            } else {
                holder.title.setBackgroundColor(Color.TRANSPARENT) // Default background
            }

            // Handle item click for expanding/collapsing and resetting highlights
            holder.itemView.setOnClickListener {
                resetHighlights()

                // Toggle expanded state
                expandedChildStates[holder.adapterPosition] = expandedChildStates.getOrDefault(holder.adapterPosition, false).not()

                // Update selected position and notify adapter
                selectedPosition = holder.adapterPosition
                notifyDataSetChanged()
            }

            // Handle expand icon click similarly
            holder.expandIcon.setOnClickListener {
                resetHighlights()

                // Toggle expanded state when the arrow icon is clicked
                expandedChildStates[holder.adapterPosition] = expandedChildStates.getOrDefault(holder.adapterPosition, false).not()

                // Update selected position and notify adapter
                selectedPosition = holder.adapterPosition
                notifyDataSetChanged()
            }

            // Setup the nested submenu RecyclerView
            holder.submenuRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.submenuRecyclerView.adapter = ChildMenuAdapter(childMenuItem.childs) { nestedChildMenuItem ->
                resetHighlights()

                // Handle submenu item clicks
                if (nestedChildMenuItem.url.isNotEmpty()) {
                    onChildMenuItemClick(nestedChildMenuItem)
                }

                // Reset highlights and update selected position
                selectedPosition = holder.adapterPosition
                notifyDataSetChanged()
            }

            // Show or hide submenu based on expansion state
            if (expandedChildStates[holder.adapterPosition] == true) {
                holder.submenuRecyclerView.visibility = View.VISIBLE
                holder.submenuRecyclerView.alpha = 0f
                holder.submenuRecyclerView.animate().alpha(1f).setDuration(300).start()
            } else {
                holder.submenuRecyclerView.animate().alpha(0f).setDuration(300).withEndAction {
                    holder.submenuRecyclerView.visibility = View.GONE
                }.start()
            }
        } else {
            // Hide the expand icon if there are no nested items
            holder.expandIcon.visibility = View.GONE
            holder.submenuRecyclerView.visibility = View.GONE

            // Highlight non-expandable items (if selected)
            if (holder.adapterPosition == selectedPosition) {
                holder.title.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_pantone_orange))
            } else {
                holder.title.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        // Handle item click if no sub-items
        holder.itemView.setOnClickListener {
            resetHighlights()

            val adapterPosition = holder.adapterPosition
            if (childMenuItem.url.isNotEmpty()) {
                onChildMenuItemClick(childMenuItem)
            }

            // Update selected position and notify adapter
            selectedPosition = adapterPosition
            notifyDataSetChanged()
        }
    }

    // Reset all highlights and expansion states
    private fun resetHighlights() {
        selectedPosition = null
        expandedChildStates.clear()  // Collapse all expanded items
    }

    override fun getItemCount(): Int = childMenuItems.size

    fun updateChildMenu(newChildMenuItems: List<ChildMenuItem>) {
        childMenuItems = newChildMenuItems
        notifyDataSetChanged()
    }
}
