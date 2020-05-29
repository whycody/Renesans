package pl.renesans.renesans.discover.recycler

import android.graphics.Bitmap
import android.net.Uri

interface DiscoverRowView {

    fun setArticleBitmapPhoto(bitmap: Bitmap)

    fun setArticleUriPhoto(uri: Uri)

    fun setArticleDrawablePhoto()

    fun setArticlePhotoSize(objectType: Int)

    fun setArticleTitle(title: String)

    fun setOnRowClickListener(pos: Int)
}