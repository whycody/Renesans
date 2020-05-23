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

        fun getFirebaseInterractor(): FirebaseContract.FirebaseInterractor?
    }

    interface ArticleActivityView {

        fun setTitle(title: String)
    }

    interface ArticlePresenter {

        fun loadContent()

        fun getFirebaseInterractor(): FirebaseContract.FirebaseInterractor?
    }
}