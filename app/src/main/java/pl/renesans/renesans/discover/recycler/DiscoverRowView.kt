package pl.renesans.renesans.discover.recycler

import android.graphics.drawable.Drawable
import android.net.Uri

interface DiscoverRowView {

    fun setArticlePhoto(drawable: Drawable)

    fun setArticleHighQualityPhoto(uri: Uri)

    fun setArticlePhotoSize(objectType: Int)

    fun setArticleTitle(title: String)

    fun setOnRowClickListener(pos: Int)
}