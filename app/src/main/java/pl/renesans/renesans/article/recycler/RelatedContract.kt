package pl.renesans.renesans.article.recycler

import android.graphics.Bitmap
import android.net.Uri

interface RelatedContract {

    interface RelatedRowView {

        fun setArticleBitmapPhoto(bitmap: Bitmap)

        fun setArticleUriPhoto(uri: Uri)

        fun setArticleTitle(title: String)

        fun setOnRowClickListener(pos: Int)
    }

    interface RelatedPresenter {

        fun onCreate()

        fun itemClicked(pos: Int)

        fun getItemCount(): Int

        fun onBindViewHolder(holder: RelatedRowHolder, position: Int)
    }
}