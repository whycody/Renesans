package pl.renesans.renesans.discover.recycler

import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.data.Article

interface DiscoverContract {

    interface DiscoverView {

        fun setDiscoverTitle(title: String)

        fun newInstance(objectType: Int, articlesListId: String): DiscoverRecyclerFragment

        fun startArticleActivity(article: Article)

        fun getObjectType(): Int

        fun loadPhoto(pos: Int, id: String)

        fun notifyDataSetChanged()
    }

    interface DiscoverRecyclerPresenter {

        fun itemClicked(pos: Int)

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