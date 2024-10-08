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
    private val onChildMenuItemClick: (ChildMenuItem) -> Unit
) : RecyclerView.Adapter<ChildMenuAdapter.ChildMenuViewHolder>() {

    private val expandedChildStates = mutableMapOf<Int, Boolean>()

    inner class ChildMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.child_menu_item_title)
        val submenuRecyclerView: RecyclerView = itemView.findViewById(R.id.submenu_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.child_menu_item, parent, false)
        return ChildMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildMenuViewHolder, position: Int) {
        val childMenuItem = childMenuItems[position]
        holder.title.text = childMenuItem.title

        holder.itemView.setOnClickListener {
            if (childMenuItem.url.isNotEmpty()) {
                onChildMenuItemClick(childMenuItem)
            }
            // Toggle the submenu for nested child items
            expandedChildStates[position] = expandedChildStates.getOrDefault(position, false).not()
            notifyItemChanged(position) // Refresh this item to show/hide nested children
        }

        // Setup the nested submenu RecyclerView if there are nested child items
        if (childMenuItem.childs?.isNotEmpty() == true) {
            holder.submenuRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.submenuRecyclerView.adapter = ChildMenuAdapter(childMenuItem.childs) { nestedChildMenuItem ->
                if (nestedChildMenuItem.url.isNotEmpty()) {
                    onChildMenuItemClick(nestedChildMenuItem)
                }
            }
            holder.submenuRecyclerView.visibility = if (expandedChildStates[position] == true) {
                holder.submenuRecyclerView.alpha = 0f
                holder.submenuRecyclerView.visibility = View.VISIBLE
                holder.submenuRecyclerView.animate().alpha(1f).setDuration(300).start()
                View.VISIBLE
            } else {
                holder.submenuRecyclerView.animate().alpha(0f).setDuration(300).withEndAction {
                    holder.submenuRecyclerView.visibility = View.GONE
                }.start()
                View.GONE
            }
        } else {
            holder.submenuRecyclerView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = childMenuItems.size

    fun updateChildMenu(newChildMenuItems: List<ChildMenuItem>) {
        childMenuItems = newChildMenuItems
        notifyDataSetChanged()
    }
}

