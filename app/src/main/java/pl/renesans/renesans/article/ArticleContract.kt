package pl.renesans.renesans.article

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.firebase.FirebaseContract

interface ArticleContract {

    interface ArticleFragmentView {

        fun getArticleObject(): Article

        fun loadBitmapToImage(bitmap: Bitmap, pos: Int)

        fun loadUriToImage(uri: Uri, pos: Int)

        fun addViewToArticleLinear(view: View)

        fun addViewToHeaderLinear(view: View)
    }

    interface ArticleActivityView {

        fun setTitle(title: String)

        fun showSuggestionBottomSheet(paragraph: Int?)
    }

    interface ArticlePresenter {

        fun loadContent()
    }
}