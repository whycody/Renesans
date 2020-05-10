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

class DiscoverRowHolder(itemView: View, val context: Context, val presenter: DiscoverRecyclerPresenter) :
    RecyclerView.ViewHolder(itemView), DiscoverRowView {

    override fun setArticleBitmapPhoto(bitmap: Bitmap) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(),
            RoundedCorners(context.resources.getDimension(R.dimen.relatedArticleViewRadius).toInt()))
        if(!bitmap.isRecycled)
            Glide.with(context).load(bitmap).apply(requestOptions).into(itemView.findViewById(R.id.articleImage))
    }

    override fun setArticleUriPhoto(uri: Uri) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(),
            RoundedCorners(context.resources.getDimension(R.dimen.relatedArticleViewRadius).toInt()))
        Glide.with(context)
            .load(uri)
            .apply(requestOptions)
            .placeholder(itemView.findViewById<ImageView>(R.id.articleImage).drawable)
            .into(itemView.findViewById(R.id.articleImage))
    }

    override fun setArticlePhotoSize(objectType: Int) {
        val articleImage = itemView.findViewById<ImageView>(R.id.articleImage)
        val articleImageHeight = articleImage.layoutParams.height
        val articleImageWidth = articleImage.layoutParams.width
        if(objectType== DiscoverRecyclerFragment.ARTS){
            articleImage.layoutParams.height = (articleImageHeight * 1.5).toInt()
        }else if(objectType== DiscoverRecyclerFragment.EVENTS||objectType== DiscoverRecyclerFragment.OTHER_ERAS){
            articleImage.layoutParams.width = (articleImageWidth * 1.8).toInt()
            articleImage.layoutParams.height = (articleImageHeight * 1.2).toInt()
        }
    }

    override fun setArticleTitle(title: String) {
        itemView.findViewById<TextView>(R.id.articleTitle).text = title
    }

    override fun setOnRowClickListener(pos: Int) {
        itemView.setOnClickListener{presenter.itemClicked(pos)}
    }
}