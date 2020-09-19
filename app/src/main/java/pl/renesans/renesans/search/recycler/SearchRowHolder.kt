package pl.renesans.renesans.search.recycler

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_search_row.view.*
import pl.renesans.renesans.R

class SearchRowHolder(itemView: View,
                      private val context: Context,
                      private val presenter: SearchContract.SearchPresenter) :
    RecyclerView.ViewHolder(itemView), SearchContract.SearchRowView {

    private var requestOptions = RequestOptions()

    init {
        requestOptions = requestOptions.transform(CenterCrop(),
            RoundedCorners(context.resources.getDimension(R.dimen.searchArticleViewRadius).toInt()))
    }

    override fun setSearchTitle(title: String) {
        itemView.searchTitle.text = title
    }

    override fun setSearchBitmapPhoto(bitmap: Bitmap) {
        Glide.with(context.applicationContext)
            .load(bitmap).apply(requestOptions)
            .placeholder(itemView.searchImage.drawable)
            .into(itemView.searchImage)
    }

    override fun setSearchUriPhoto(uri: Uri) {
        Glide.with(context.applicationContext)
            .load(uri)
            .apply(requestOptions)
            .placeholder(itemView.searchImage.drawable)
            .into(itemView.searchImage)
    }

    override fun setSearchDrawablePhoto(drawable: Drawable) {
        Glide.with(context.applicationContext)
            .load(drawable)
            .apply(requestOptions)
            .placeholder(itemView.searchImage.drawable)
            .into(itemView.searchImage)
    }

    override fun setOnClickListener(pos: Int) =
        itemView.setOnClickListener{ presenter.itemClicked(pos) }

    override fun setOnDeleteViewClickListener(pos: Int) =
        itemView.deleteView.setOnClickListener{ presenter.deleteItemClicked(pos) }

    override fun setVisibilityOfDeleteBtn(visibility: Int) {
        itemView.deleteView.visibility = visibility
    }
}