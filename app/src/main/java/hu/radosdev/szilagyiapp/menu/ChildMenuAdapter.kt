package hu.radosdev.szilagyiapp.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.entity.ChildMenuItem

class ChildMenuAdapter(
    private var childMenuItems: List<ChildMenuItem>,
    private val onChildMenuItemClick: (ChildMenuItem) -> Unit // Click listener for child menu items
) : RecyclerView.Adapter<ChildMenuAdapter.ChildMenuViewHolder>() {

    // Store expanded state for each child item
    private val expandedChildStates = mutableMapOf<Int, Boolean>() // Map to track expanded state of child items

    inner class ChildMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.child_menu_item_title)
        val submenuRecyclerView: RecyclerView = itemView.findViewById(R.id.submenu_recycler_view) // Nested child RecyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.child_menu_item, parent, false)
        return ChildMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildMenuViewHolder, position: Int) {
        val childMenuItem = childMenuItems[position]
        holder.title.text = childMenuItem.title

        // Handle item click for child menu
        holder.itemView.setOnClickListener {
            if (childMenuItem.url.isNotEmpty()) {
                onChildMenuItemClick(childMenuItem) // Notify click for valid URL
            }
            // Toggle the submenu for nested child items
            expandedChildStates[position] = expandedChildStates.getOrDefault(position, false).not() // Toggle expanded state
            notifyItemChanged(position) // Refresh this item to show/hide nested children
        }

        // Setup the nested submenu RecyclerView if there are nested child items
        if (childMenuItem.childs?.isNotEmpty() == true) {
            holder.submenuRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.submenuRecyclerView.adapter = ChildMenuAdapter(childMenuItem.childs) { nestedChildMenuItem ->
                if (nestedChildMenuItem.url.isNotEmpty()) {
                    onChildMenuItemClick(nestedChildMenuItem) // Notify click for valid URL
                }
            }
            holder.submenuRecyclerView.visibility = if (expandedChildStates[position] == true) View.VISIBLE else View.GONE // Show/Hide based on expanded state
        } else {
            holder.submenuRecyclerView.visibility = View.GONE // Hide if no nested items
        }
    }

    override fun getItemCount(): Int = childMenuItems.size

    fun updateChildMenu(newChildMenuItems: List<ChildMenuItem>) {
        childMenuItems = newChildMenuItems
        notifyDataSetChanged()
    }
}

