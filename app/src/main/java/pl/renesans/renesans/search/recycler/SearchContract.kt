package pl.renesans.renesans.search.recycler

import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.data.Article

interface SearchContract {

    interface SearchView {

        fun startArticleActivity(article: Article)
    }

    interface SearchRowView {

        fun setSearchTitle(title: String)

        fun setSearchBitmapPhoto(bitmap: Bitmap)

        fun setSearchUriPhoto(uri: Uri)

        fun setOnClickListener(pos: Int)
    }

    interface SearchPresenter {

        fun onCreate()

        fun getAllArticles(): List<Article>

        fun getCurrentArticlesList(): List<Article>

        fun setCurentArticlesList(articlesList: List<Article>)

        fun itemClicked(pos: Int)

        fun getItemCount(): Int

        fun onBindViewHolder(holder: SearchRowHolder, position: Int)
    }
}