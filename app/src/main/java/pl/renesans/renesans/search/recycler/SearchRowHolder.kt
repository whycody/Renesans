package pl.renesans.renesans.search.recycler

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import pl.renesans.renesans.R

class SearchRowHolder(itemView: View, val context: Context, val presenter: SearchContract.SearchPresenter) :
    RecyclerView.ViewHolder(itemView), SearchContract.SearchRowView {

    override fun setSearchTitle(title: String) {
        itemView.findViewById<TextView>(R.id.searchTitle).text = title
    }

    override fun setSearchBitmapPhoto(bitmap: Bitmap) {
        if(bitmap.isRecycled) return
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(),
            RoundedCorners(context.resources.getDimension(R.dimen.searchArticleViewRadius).toInt()))
        Glide.with(context)
            .load(bitmap).apply(requestOptions)
            .placeholder(itemView.findViewById<ImageView>(R.id.searchImage).drawable)
            .into(itemView.findViewById(R.id.searchImage))
    }

    override fun setSearchUriPhoto(uri: Uri) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(),
            RoundedCorners(context.resources.getDimension(R.dimen.searchArticleViewRadius).toInt()))
        Glide.with(context.applicationContext)
            .load(uri)
            .apply(requestOptions)
            .placeholder(itemView.findViewById<ImageView>(R.id.searchImage).drawable)
            .into(itemView.findViewById(R.id.searchImage))
    }

    override fun setSearchDrawablePhoto(drawable: Drawable) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(),
            RoundedCorners(context.resources.getDimension(R.dimen.searchArticleViewRadius).toInt()))
        Glide.with(context.applicationContext)
            .load(drawable)
            .apply(requestOptions)
            .placeholder(itemView.findViewById<ImageView>(R.id.searchImage).drawable)
            .into(itemView.findViewById(R.id.searchImage))
    }

    override fun setOnClickListener(pos: Int) {
        itemView.setOnClickListener{ presenter.itemClicked(pos) }
    }

    override fun setOnDeleteViewClickListener(pos: Int) {
        itemView.findViewById<ImageButton>(R.id.deleteView).setOnClickListener{
            presenter.deleteItemClicked(pos)
        }
    }

    override fun setVisibilityOfDeleteBtn(visibility: Int) {
        itemView.findViewById<ImageButton>(R.id.deleteView).visibility = visibility
    }
}