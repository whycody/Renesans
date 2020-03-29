package pl.renesans.renesans.discover.recycler

import android.graphics.Bitmap
import android.net.Uri

interface DiscoverRowView {

    fun setArticlePhoto(bitmap: Bitmap)

    fun setArticleHighQualityPhoto(uri: Uri)

    fun setArticlePhotoSize(objectType: Int)

    fun setArticleTitle(title: String)

    fun setOnRowClickListener(pos: Int)
}