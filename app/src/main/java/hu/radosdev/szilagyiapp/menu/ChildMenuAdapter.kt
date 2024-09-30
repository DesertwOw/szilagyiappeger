package hu.radosdev.szilagyiapp.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.entity.ChildMenuItem

class ChildMenuAdapter(
    private var childMenuItems: List<ChildMenuItem>, // List of ChildMenuItem
    private val onChildMenuItemClick: (ChildMenuItem) -> Unit // Click listener for child menu items
) : RecyclerView.Adapter<ChildMenuAdapter.ChildMenuViewHolder>() {

    inner class ChildMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.child_menu_item_title)
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
            onChildMenuItemClick(childMenuItem)
        }
    }

    override fun getItemCount(): Int = childMenuItems.size

    fun updateChildMenu(newChildMenuItems: List<ChildMenuItem>) {
        childMenuItems = newChildMenuItems
        notifyDataSetChanged()
    }
}
