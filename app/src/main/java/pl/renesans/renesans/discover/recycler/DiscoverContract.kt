package pl.renesans.renesans.discover.recycler

import android.graphics.Bitmap
import android.net.Uri

interface DiscoverContract {

    interface DiscoverView {

        fun newInstance(objectType: Int, articlesListId: String): DiscoverRecyclerFragment

        fun notifyDataSetChanged()
    }

    interface DiscoverRecyclerPresenter {

        fun itemClicked(pos: Int)

        fun getArticlesListTitle(): String

        fun getItemCount(): Int

        fun onBindViewHolder(holder: DiscoverRowHolder, position: Int)
    }

    interface DiscoverRowView {

        fun setArticleBitmapPhoto(bitmap: Bitmap)

        fun setArticleUriPhoto(uri: Uri)

        fun setArticleDrawablePhoto()

        fun setArticlePhotoSize(objectType: Int)

        fun setArticleTitle(title: String)

        fun setOnRowClickListener(pos: Int)
    }
}