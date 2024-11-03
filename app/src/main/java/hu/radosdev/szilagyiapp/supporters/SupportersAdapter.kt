package hu.radosdev.szilagyiapp.supporters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.entity.Supporter

class SupportersAdapter(private val context: Context, private val supporters: List<Supporter>) : BaseAdapter() {

    override fun getCount(): Int {
        return supporters.size
    }

    override fun getItem(position: Int): Supporter {
        return supporters[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.supporter_item, parent, false)

        val supporter = getItem(position)

        val nameTextView = view.findViewById<TextView>(R.id.supporter_name)
        val logoImageView = view.findViewById<ImageView>(R.id.supporter_image)

        nameTextView.text = supporter.name

        // Set the image or placeholder
        if (supporter.drawableRes != null) {
            logoImageView.setImageResource(supporter.drawableRes)
        }

        // Set an OnClickListener on the entire item view
        view.setOnClickListener {
            supporter.url?.let { url ->
                // Start an Intent to open the URL in a web browser
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(browserIntent)
            }
        }

        return view
    }

}
