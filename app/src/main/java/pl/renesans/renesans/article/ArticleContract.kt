package pl.renesans.renesans.article

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import pl.renesans.renesans.data.Article

interface ArticleContract {

    interface ArticleView {

        fun getArticleObject(): Article

        fun setTitle(title: String)

        fun loadBitmapToImage(bitmap: Bitmap, pos: Int)

        fun loadUriToImage(uri: Uri, pos: Int)

        fun addViewToArticleLinear(view: View)

        fun addViewToHeaderLinear(view: View)
    }

    interface ArticlePresenter {

        fun loadContent()
    }
}