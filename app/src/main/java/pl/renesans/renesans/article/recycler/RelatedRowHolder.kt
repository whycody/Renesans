package pl.renesans.renesans.article.recycler

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

class RelatedRowHolder(itemView: View, val context: Context, val presenter: RelatedContract.RelatedPresenter)
    : RecyclerView.ViewHolder(itemView), RelatedContract.RelatedRowView {

    override fun setArticleBitmapPhoto(bitmap: Bitmap) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(),
            RoundedCorners(context.resources.getDimension(R.dimen.relatedArticleViewRadius).toInt()))
        Glide.with(context).load(bitmap).apply(requestOptions).into(itemView.findViewById(R.id.articleImage))
    }

    override fun setArticleUriPhoto(uri: Uri) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(),
            RoundedCorners(context.resources.getDimension(R.dimen.relatedArticleViewRadius).toInt()))
        Glide.with(context.applicationContext)
            .load(uri)
            .apply(requestOptions)
            .placeholder(itemView.findViewById<ImageView>(R.id.articleImage).drawable)
            .into(itemView.findViewById(R.id.articleImage))
    }

    override fun setArticleTitle(title: String) {
        itemView.findViewById<TextView>(R.id.articleTitle).text = title
    }

    override fun setOnRowClickListener(pos: Int) {
        itemView.setOnClickListener{presenter.itemClicked(pos)}
    }
}