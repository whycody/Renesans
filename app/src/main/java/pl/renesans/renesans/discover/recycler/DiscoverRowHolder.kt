package pl.renesans.renesans.discover.recycler

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class DiscoverRowHolder(itemView: View, val context: Context, val presenter: DiscoverRecyclerPresenter) :
    RecyclerView.ViewHolder(itemView), DiscoverRowView {

    override fun setArticlePhoto(drawable: Drawable) {
        itemView.findViewById<ImageView>(R.id.articleImage).setImageDrawable(drawable)
    }

    override fun setArticlePhotoSize(objectType: Int) {
        val articleImage = itemView.findViewById<ImageView>(R.id.articleImage)
        val articleImageHeight = articleImage.layoutParams.height
        val articleImageWidth = articleImage.layoutParams.width
        if(objectType==DiscoverRecyclerFragment.ARTS){
            articleImage.layoutParams.height = (articleImageHeight * 1.5).toInt()
        }else if(objectType==DiscoverRecyclerFragment.EVENTS||objectType==DiscoverRecyclerFragment.OTHER_ERAS){
            articleImage.layoutParams.width = (articleImageWidth * 1.8).toInt()
            articleImage.layoutParams.height = (articleImageHeight * 1.2).toInt()
        }
    }

    override fun setArticleTitle(title: String) {
        itemView.findViewById<TextView>(R.id.articleTitle).text = title
    }

    override fun setOnRowClickListener(pos: Int) {
        presenter.itemClicked(pos)
    }
}