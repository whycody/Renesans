package pl.renesans.renesans.utility.article.views

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.data.Photo

interface ArticleViewsUtility {

    fun getHeaderTitleView(text: String?): TextView

    fun getHeaderContentView(text: String?): TextView

    fun getParagraphTitleView(text: String?): TextView

    fun getParagraphContentView(text: String?): TextView

    fun getInvisibleView(bigMargin: Boolean): View

    fun getParagraphImageView(photo: Photo): ImageView

    fun getParagraphImageDescriptionView(text: String?): TextView

    fun getRelatedRecyclerView(): RecyclerView
}