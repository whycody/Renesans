package pl.renesans.renesans.map.recycler

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class LocationRowHolder(itemView: View, val presenter: LocationPresenter? = null) :
    RecyclerView.ViewHolder(itemView), LocationRowView {

    override fun setDrawable(drawable: Drawable) {
        itemView.findViewById<TextView>(R.id.locationRow).background = drawable
    }

    override fun setText(text: String) {
        itemView.findViewById<TextView>(R.id.locationRow).text = text
    }

    override fun setOnRowClickListener(pos: Int) {
        itemView.findViewById<LinearLayout>(R.id.locationLayout).setOnClickListener{
            presenter?.itemClicked(pos)
        }
    }

    override fun setTextColor(color: Int) {
        itemView.findViewById<TextView>(R.id.locationRow).setTextColor(color)
    }
}