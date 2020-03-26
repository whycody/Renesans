package pl.renesans.renesans.discover.recycler

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class DiscoverRowHolder(itemView: View, val presenter: DiscoverRecyclerPresenter) :
    RecyclerView.ViewHolder(itemView), DiscoverRowView {

    override fun setArticlePhoto(drawable: Drawable) {
        itemView.findViewById<ImageView>(R.id.articleImage).setImageDrawable(drawable)
    }

    override fun setArticleTitle(title: String) {
        itemView.findViewById<TextView>(R.id.articleTitle).text = title
    }

    override fun setOnRowClickListener(pos: Int) {
        presenter.itemClicked(pos)
    }
}