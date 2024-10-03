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

    // Tároljuk az éppen megnyitott menü pozícióját
    private var expandedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)

        // Főmenü elem kattintása
        holder.itemView.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (expandedPosition == adapterPosition) {
                    // Ha már ki van bontva, összecsukjuk
                    expandedPosition = RecyclerView.NO_POSITION
                } else {
                    // Ha másik elem van kinyitva, azt becsukjuk, és ezt nyitjuk ki
                    expandedPosition = adapterPosition
                }
                notifyDataSetChanged() // Frissítjük a megjelenést
                onMainMenuItemClick(menuItem)
            }
        }

        // A child menü láthatóságának beállítása
        val isExpanded = holder.adapterPosition == expandedPosition
        holder.submenuRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE


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

            // Child menü elemek beállítása
            if (mainMenuItem.childs?.isNotEmpty() == true) {
                submenuRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
                submenuRecyclerView.adapter = ChildMenuAdapter(mainMenuItem.childs) { childMenuItem ->
                    onChildMenuItemClick(childMenuItem)
                }
            }
        }
    }
}
