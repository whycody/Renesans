package pl.renesans.renesans.discover.recycler

import android.graphics.drawable.Drawable

interface DiscoverRowView {

    fun setArticlePhoto(drawable: Drawable)

    fun setArticlePhotoSize(objectType: Int)

    fun setArticleTitle(title: String)

    fun setOnRowClickListener(pos: Int)
}