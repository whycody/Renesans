package pl.renesans.renesans.discover.recycler

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import pl.renesans.renesans.R

class DiscoverRowHolder(itemView: View,
                        private val context: Context?,
                        private val presenter: DiscoverContract.DiscoverRecyclerPresenter) :
    RecyclerView.ViewHolder(itemView), DiscoverContract.DiscoverRowView {

    private val requestOptions: RequestOptions
    private val articleImageHeight: Int
    private val articleImageWidth: Int

    init {
        requestOptions = getRequestOptions()
        articleImageHeight = context?.resources?.getDimension(R.dimen.discoverImageHeight)!!.toInt()
        articleImageWidth = context.resources.getDimension(R.dimen.discoverImageWidth).toInt()
    }

    private fun getRequestOptions() = RequestOptions().transform(CenterCrop(),
        RoundedCorners(context?.resources?.getDimension(R.dimen.relatedArticleViewRadius)!!.toInt()))

    override fun setArticleBitmapPhoto(bitmap: Bitmap) {
        if(context == null || bitmap.isRecycled) return
        Glide.with(context)
            .load(bitmap)
            .apply(requestOptions)
            .into(itemView.findViewById(R.id.articleImage))
    }

    override fun setArticleUriPhoto(uri: Uri) {
        if(context == null) return
        Glide.with(context)
            .load(uri)
            .apply(requestOptions)
            .placeholder(itemView.findViewById<ImageView>(R.id.articleImage).drawable)
            .into(itemView.findViewById(R.id.articleImage))
    }

    override fun setArticleDrawablePhoto() {
        if(context == null) return
        Glide.with(context)
            .load(R.drawable.sh_discover_recycler_row)
            .into(itemView.findViewById(R.id.articleImage))
    }

    override fun setArticlePhotoSize(objectType: Int) {
        val articleImage = itemView.findViewById<ImageView>(R.id.articleImage)
        if(objectType == DiscoverRecyclerFragment.ARTS)
            setArtsDiscoverRecyclerHeight(articleImage)
        else if(objectType != DiscoverRecyclerFragment.PEOPLE)
            setEventsDiscoverRecyclerDimensions(articleImage)
    }

    private fun setArtsDiscoverRecyclerHeight(articleImage: ImageView) {
        articleImage.layoutParams.height = (articleImageHeight * 1.5).toInt()
    }

    private fun setEventsDiscoverRecyclerDimensions(articleImage: ImageView) {
        articleImage.layoutParams.width = (articleImageWidth * 1.8).toInt()
        articleImage.layoutParams.height = (articleImageHeight * 1.2).toInt()
    }

    override fun setArticleTitle(title: String) {
        itemView.findViewById<TextView>(R.id.articleTitle).text = title
    }

    override fun setOnRowClickListener(pos: Int) =
        itemView.setOnClickListener{presenter.itemClicked(pos)}
}