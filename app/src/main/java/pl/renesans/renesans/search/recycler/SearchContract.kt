package pl.renesans.renesans.search.recycler

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticleItem

interface SearchContract {

    interface SearchView {

        fun startArticleActivity(article: Article)

        fun holderIsVisible(pos: Int): Boolean
    }

    interface SearchRowView {

        fun setSearchTitle(title: String)

        fun setSearchBitmapPhoto(bitmap: Bitmap)

        fun setSearchUriPhoto(uri: Uri)

        fun setSearchDrawablePhoto(drawable: Drawable)

        fun setOnClickListener(pos: Int)
    }

    interface SearchPresenter {

        fun onCreate()

        fun getAllArticles(): List<ArticleItem>

        fun getCurrentArticlesList(): List<ArticleItem>

        fun setCurrentArticlesList(articlesList: List<ArticleItem>)

        fun itemClicked(pos: Int)

        fun getItemCount(): Int

        fun onBindViewHolder(holder: SearchRowHolder, position: Int)
    }
}