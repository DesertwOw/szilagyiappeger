package hu.radosdev.szilagyiapp.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.entity.ChildMenuItem

class ChildMenuAdapter(
    private var childMenuItems: List<ChildMenuItem>,
    private val onChildMenuItemClick: (ChildMenuItem) -> Unit
) : RecyclerView.Adapter<ChildMenuAdapter.ChildMenuViewHolder>() {

    private val expandedChildStates = mutableMapOf<Int, Boolean>()

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
        val childMenuItem = childMenuItems[position]
        holder.title.text = childMenuItem.title

        // Check if the child has nested sub-items (grandchildren)
        if (childMenuItem.childs?.isNotEmpty() == true) {
            // Show the expand icon when there are nested items
            holder.expandIcon.visibility = View.VISIBLE

            // Set the rotation of the arrow based on expansion state
            holder.expandIcon.rotation = if (expandedChildStates[position] == true) 180f else 0f

            holder.itemView.setOnClickListener {
                // Toggle expanded state
                expandedChildStates[position] = expandedChildStates.getOrDefault(position, false).not()
                notifyItemChanged(position)
            }

            holder.expandIcon.setOnClickListener {
                // Same toggle action when arrow icon is clicked
                expandedChildStates[position] = expandedChildStates.getOrDefault(position, false).not()
                notifyItemChanged(position)
            }

            // Setup the nested submenu RecyclerView
            holder.submenuRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.submenuRecyclerView.adapter = ChildMenuAdapter(childMenuItem.childs) { nestedChildMenuItem ->
                if (nestedChildMenuItem.url.isNotEmpty()) {
                    onChildMenuItemClick(nestedChildMenuItem)
                }
            }

            // Show or hide submenu based on expansion state
            if (expandedChildStates[position] == true) {
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
        }

        // Handle item click if no sub-items
        holder.itemView.setOnClickListener {
            if (childMenuItem.url.isNotEmpty()) {
                onChildMenuItemClick(childMenuItem)
            }
        }
    }

    override fun getItemCount(): Int = childMenuItems.size

    fun updateChildMenu(newChildMenuItems: List<ChildMenuItem>) {
        childMenuItems = newChildMenuItems
        notifyDataSetChanged()
    }
}
