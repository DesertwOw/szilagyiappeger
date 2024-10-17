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

    private val expandedChildStates = mutableMapOf<Int, Boolean>()
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
        val childMenuItem = childMenuItems[holder.bindingAdapterPosition]
        holder.title.text = childMenuItem.title

        if (childMenuItem.childs?.isNotEmpty() == true) {
            holder.expandIcon.visibility = View.VISIBLE

            holder.expandIcon.rotation = if (expandedChildStates[holder.bindingAdapterPosition] == true) Constants.ANIMATE_ANGLE else Constants.SUBMENU_RECYCLER_VIEW_ALPHA

            if (holder.bindingAdapterPosition == selectedPosition && expandedChildStates[holder.bindingAdapterPosition] == true) {
                holder.title.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_pantone_orange))
                holder.expandIcon.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_pantone_orange))
            } else {
                holder.title.setBackgroundColor(Color.TRANSPARENT)
                holder.expandIcon.setBackgroundColor(Color.TRANSPARENT)
            }

            holder.itemView.setOnClickListener {
                val previousSelectedPosition = selectedPosition
                selectedPosition = holder.bindingAdapterPosition

                expandedChildStates[holder.bindingAdapterPosition] = expandedChildStates.getOrDefault(holder.bindingAdapterPosition, false).not()

                if (previousSelectedPosition != null) {
                    notifyItemChanged(previousSelectedPosition)
                }
                notifyItemChanged(holder.bindingAdapterPosition)
            }

            holder.expandIcon.setOnClickListener {
                val previousSelectedPosition = selectedPosition
                selectedPosition = holder.bindingAdapterPosition

                expandedChildStates[holder.bindingAdapterPosition] = expandedChildStates.getOrDefault(holder.bindingAdapterPosition, false).not()

                if (previousSelectedPosition != null) {
                    notifyItemChanged(previousSelectedPosition)
                }
                notifyItemChanged(holder.bindingAdapterPosition)
            }

            holder.submenuRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.submenuRecyclerView.adapter = ChildMenuAdapter(childMenuItem.childs) { nestedChildMenuItem ->
                resetHighlights()

                if (nestedChildMenuItem.url.isNotEmpty()) {
                    onChildMenuItemClick(nestedChildMenuItem)
                }

                val previousSelectedPosition = selectedPosition
                selectedPosition = holder.bindingAdapterPosition

                if (previousSelectedPosition != null) {
                    notifyItemChanged(previousSelectedPosition)
                }
                notifyItemChanged(holder.bindingAdapterPosition)
            }

            if (expandedChildStates[holder.bindingAdapterPosition] == true) {
                holder.submenuRecyclerView.visibility = View.VISIBLE
                holder.submenuRecyclerView.alpha = Constants.SUBMENU_RECYCLER_VIEW_ALPHA
                holder.submenuRecyclerView.animate().alpha(1f).setDuration(Constants.ANIMATE_DURATION).start()
            } else {
                holder.submenuRecyclerView.animate().alpha(Constants.SUBMENU_RECYCLER_VIEW_ALPHA).setDuration(Constants.ANIMATE_DURATION).withEndAction {
                    holder.submenuRecyclerView.visibility = View.GONE
                }.start()
            }
        } else {
            holder.expandIcon.visibility = View.GONE
            holder.submenuRecyclerView.visibility = View.GONE

            if (holder.bindingAdapterPosition == selectedPosition) {
                holder.title.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_pantone_orange))
            } else {
                holder.title.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        holder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.bindingAdapterPosition

            if (childMenuItem.url.isNotEmpty()) {
                onChildMenuItemClick(childMenuItem)
            }

            if (previousSelectedPosition != null) {
                notifyItemChanged(previousSelectedPosition)
            }
            notifyItemChanged(holder.bindingAdapterPosition)
        }
    }

    private fun resetHighlights() {
        val previousSelectedPosition = selectedPosition
        selectedPosition = null
        expandedChildStates.clear()

        previousSelectedPosition?.let { notifyItemChanged(it) }
    }

    override fun getItemCount(): Int = childMenuItems.size
}
