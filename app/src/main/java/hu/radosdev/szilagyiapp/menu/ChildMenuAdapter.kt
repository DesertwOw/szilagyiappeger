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
import hu.radosdev.szilagyiapp.util.Constants

class ChildMenuAdapter(
    private var childMenuItems: List<ChildMenuItem>,
    private val onChildMenuItemClick: (ChildMenuItem) -> Unit
) : RecyclerView.Adapter<ChildMenuAdapter.ChildMenuViewHolder>() {

    private var expandedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.child_menu_item, parent, false)
        return ChildMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildMenuViewHolder, position: Int) {
        val childMenuItem = childMenuItems[position]
        holder.bind(childMenuItem)

        holder.itemView.setOnClickListener {
            val wasExpanded = expandedPosition == holder.bindingAdapterPosition

            if (expandedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(expandedPosition)
            }

            expandedPosition = if (wasExpanded) {
                RecyclerView.NO_POSITION
            } else {
                holder.bindingAdapterPosition
            }

            notifyItemChanged(holder.bindingAdapterPosition)

            if (childMenuItem.childs.isNullOrEmpty() && childMenuItem.url.isNotEmpty()) {
                onChildMenuItemClick(childMenuItem)
            }
        }

        val isExpanded = holder.bindingAdapterPosition == expandedPosition
        holder.submenuRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE

        if (isExpanded) {
            holder.submenuRecyclerView.alpha = Constants.SUBMENU_RECYCLER_VIEW_ALPHA
            holder.submenuRecyclerView.animate().alpha(1f).setDuration(Constants.ANIMATE_DURATION).start()

            // Set up nested adapter if there are child items
            holder.submenuRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.submenuRecyclerView.adapter = ChildMenuAdapter(childMenuItem.childs ?: emptyList()) { nestedChild ->
                onChildMenuItemClick(nestedChild)
            }
        }

        holder.titleTextView.setBackgroundColor(
            if (isExpanded) {
                ContextCompat.getColor(holder.itemView.context, R.color.primary_pantone_orange)
            } else {
                Color.TRANSPARENT
            }
        )

        holder.expandIcon.setBackgroundColor(
            if (isExpanded) {
                ContextCompat.getColor(holder.itemView.context, R.color.primary_pantone_orange)
            } else {
                Color.TRANSPARENT
            }
        )
    }

    override fun getItemCount(): Int = childMenuItems.size

    inner class ChildMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.child_menu_item_title)
        val expandIcon: ImageView = itemView.findViewById(R.id.menu_item_expand_icon)
        val submenuRecyclerView: RecyclerView = itemView.findViewById(R.id.submenu_recycler_view)

        fun bind(childMenuItem: ChildMenuItem) {
            titleTextView.text = childMenuItem.title

            if (!childMenuItem.childs.isNullOrEmpty()) {
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
