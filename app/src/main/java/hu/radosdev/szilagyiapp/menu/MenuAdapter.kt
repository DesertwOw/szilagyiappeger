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
import hu.radosdev.szilagyiapp.MainActivity
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.entity.MainMenuItem
import hu.radosdev.szilagyiapp.data.entity.ChildMenuItem
import hu.radosdev.szilagyiapp.supporters.SupportersActivity
import hu.radosdev.szilagyiapp.util.Constants

class MenuAdapter(
    private var menuItems: MutableList<MainMenuItem>, // Change to MutableList
    private val onChildMenuItemClick: (ChildMenuItem) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private var expandedPosition: Int = RecyclerView.NO_POSITION

    init {
        val supportersItem = MainMenuItem(title = "TÁMOGATÓINK", childs = null)
        menuItems.add(supportersItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)

        holder.itemView.setOnClickListener {
            if (menuItem.title == "TÁMOGATÓINK") {
                val intent = Intent(context, SupportersActivity::class.java)
                context.startActivity(intent)
            } else {
                val wasExpanded = expandedPosition == holder.bindingAdapterPosition

                if (expandedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(expandedPosition)
                }

                expandedPosition = if (wasExpanded) {
                    RecyclerView.NO_POSITION
                } else {
                    holder.bindingAdapterPosition
                }

                notifyItemChanged(expandedPosition)
            }
        }


        val isExpanded = holder.bindingAdapterPosition == expandedPosition
        holder.submenuRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE

        if (isExpanded) {
            holder.submenuRecyclerView.alpha = Constants.SUBMENU_RECYCLER_VIEW_ALPHA
            holder.submenuRecyclerView.animate().alpha(1f).setDuration(Constants.ANIMATE_DURATION).start()
        }

        if (menuItem.title == "FŐOLDAL") {
            holder.itemView.setOnClickListener {
                val childMenuItem = ChildMenuItem(url = "https://www.szilagyi-eger.hu", title = "FŐOLDAL", childs = null)
                onChildMenuItemClick(childMenuItem)
            }
        } else {
            if (isExpanded) {
                holder.submenuRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
                holder.submenuRecyclerView.adapter = ChildMenuAdapter(menuItem.childs ?: emptyList()) { childMenuItem ->
                    val url = childMenuItem.url
                    if (!url.contains(Constants.CONTAINS_URL_STRING)) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } else {
                        onChildMenuItemClick(childMenuItem)
                    }

                    expandedPosition = RecyclerView.NO_POSITION
                    notifyItemChanged(holder.bindingAdapterPosition)
                }
            }
        }

        holder.titleTextView.setBackgroundColor(
            if (holder.bindingAdapterPosition == expandedPosition) {
                ContextCompat.getColor(holder.itemView.context, R.color.primary_pantone_orange)
            } else {
                Color.TRANSPARENT
            }
        )

        holder.expandIcon.setBackgroundColor(
            if (holder.bindingAdapterPosition == expandedPosition) {
                ContextCompat.getColor(holder.itemView.context, R.color.primary_pantone_orange)
            } else {
                Color.TRANSPARENT
            }
        )
    }

    fun updateMenu(items: List<MainMenuItem>) {
        menuItems = items.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.menu_item_title)
        val expandIcon: ImageView = itemView.findViewById(R.id.menu_item_expand_icon)
        val submenuRecyclerView: RecyclerView = itemView.findViewById(R.id.submenu_recycler_view)

        fun bind(mainMenuItem: MainMenuItem) {
            titleTextView.text = mainMenuItem.title

            if (!mainMenuItem.childs.isNullOrEmpty()) {
                expandIcon.visibility = View.VISIBLE
                val isExpanded = bindingAdapterPosition == expandedPosition

                expandIcon.rotation = if (isExpanded) Constants.ANIMATE_ANGLE else Constants.SUBMENU_RECYCLER_VIEW_ALPHA
                expandIcon.animate().rotation(if (isExpanded) Constants.ANIMATE_ANGLE else Constants.SUBMENU_RECYCLER_VIEW_ALPHA).setDuration(Constants.ANIMATE_DURATION).start()
            } else {
                expandIcon.visibility = View.GONE
            }
        }
    }
}
