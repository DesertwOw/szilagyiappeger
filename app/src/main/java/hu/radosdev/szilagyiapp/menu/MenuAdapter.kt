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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)

        // Handle main menu item click
        holder.itemView.setOnClickListener {
            onMainMenuItemClick(menuItem)
        }
    }

    override fun getItemCount(): Int = menuItems.size

    fun updateMenu(items: List<MainMenuItem>) {
        menuItems = items
        notifyDataSetChanged()
    }

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.menu_item_title)
        private val submenuRecyclerView: RecyclerView = itemView.findViewById(R.id.submenu_recycler_view)

        fun bind(mainMenuItem: MainMenuItem) {
            titleTextView.text = mainMenuItem.title

            // Handle submenu if available
            if (mainMenuItem.childs?.isNotEmpty() == true) {
                submenuRecyclerView.visibility = View.VISIBLE
                submenuRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
                submenuRecyclerView.adapter = ChildMenuAdapter(mainMenuItem.childs) { childMenuItem ->
                    onChildMenuItemClick(childMenuItem)
                }
            } else {
                submenuRecyclerView.visibility = View.GONE
            }
        }
    }
}
