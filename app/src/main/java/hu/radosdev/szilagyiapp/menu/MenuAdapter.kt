package hu.radosdev.szilagyiapp.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.entity.MainMenuItem
import hu.radosdev.szilagyiapp.data.entity.ChildMenuItem

class MenuAdapter(
    private var menuItems: List<MainMenuItem>,
    private val onMainMenuItemClick: (MainMenuItem) -> Unit,
    private val onChildMenuItemClick: (ChildMenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private var expandedPosition: Int = RecyclerView.NO_POSITION // Track expanded position for main menu items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)

        // Handle main menu item click
        holder.itemView.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                // Toggle the expanded state for main menu items
                expandedPosition = if (expandedPosition == adapterPosition) {
                    RecyclerView.NO_POSITION // Collapse if already expanded
                } else {
                    adapterPosition // Expand the selected item
                }
                notifyDataSetChanged() // Refresh the adapter to update visibility
                onMainMenuItemClick(menuItem)
            }
        }

        // Set the visibility of the submenu based on expansion state
        val isExpanded = holder.adapterPosition == expandedPosition
        holder.submenuRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Setup child menu items adapter only if expanded
        if (isExpanded) {
            holder.submenuRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.submenuRecyclerView.adapter = ChildMenuAdapter(menuItem.childs ?: emptyList()) { childMenuItem ->
                // Handle child menu item click
                if (childMenuItem.url.isNotEmpty()) {
                    onChildMenuItemClick(childMenuItem)
                }
            }
        }
    }

    override fun getItemCount(): Int = menuItems.size

    fun updateMenu(items: List<MainMenuItem>) {
        menuItems = items
        notifyDataSetChanged()
    }

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.menu_item_title)
        val submenuRecyclerView: RecyclerView = itemView.findViewById(R.id.submenu_recycler_view)

        fun bind(mainMenuItem: MainMenuItem) {
            titleTextView.text = mainMenuItem.title
        }
    }
}

