package pl.renesans.renesans.sources

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

class SourcesRowHolder(itemView: View, val context: Context,
                       val presenter: SourcesContract.SourcesPresenter) : RecyclerView.ViewHolder(itemView),
    SourcesContract.SourcesRowView {

    override fun setSourceBitmapPhoto(bitmap: Bitmap) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(),
            RoundedCorners(context.resources.getDimension(R.dimen.relatedArticleViewRadius).toInt()))
        Glide.with(context).load(bitmap).apply(requestOptions).into(itemView.findViewById(R.id.sourceImage))
    }

    override fun setSourceUriPhoto(uri: Uri) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(),
            RoundedCorners(context.resources.getDimension(R.dimen.relatedArticleViewRadius).toInt()))
        Glide.with(context)
            .load(uri)
            .apply(requestOptions)
            .placeholder(itemView.findViewById<ImageView>(R.id.sourceImage).drawable)
            .into(itemView.findViewById(R.id.sourceImage))
    }

    override fun setTitle(title: String) {
        itemView.findViewById<TextView>(R.id.sourceTitle).text = title
    }

    override fun setDescription(description: String) {
        itemView.findViewById<TextView>(R.id.sourcePage).text = description
    }

    override fun setOnClickListener(pos: Int) {
        itemView.findViewById<ImageView>(R.id.sourceImage).setOnClickListener{
            presenter.itemClicked(pos)
        }
    }
}