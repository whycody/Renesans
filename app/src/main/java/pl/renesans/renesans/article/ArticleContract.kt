package pl.renesans.renesans.article

import android.view.View
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.Paragraph
import pl.renesans.renesans.data.Photo

interface ArticleContract {

    interface ArticleFragmentView: ArticleActivityView {

        fun setDefaultHeightOfArticleImage()

        fun getArticleObject(): Article

        fun setArticleImageHeight(height: Int)

        fun loadPhoto(pos: Int, id: String)

        fun addHeaderTitleView(text: String?)

        fun addHeaderContentView(text: String?)

        fun tryToAddRelatedRecyclerView()

        fun addParagraphTitleView(text: String?, index: Int? = null)

        fun addParagraphContentView(text: String?, index: Int? = null)

        fun getInvisibleView(bigMargin: Boolean): View

        fun saveInvisibleView(index: Int, view: View)

        fun copyTextToClipboard(text: String)

        fun addParagraphImageView(photo: Photo)

        fun addParagraphImageDescriptionView(text: String?)

        fun addViewToArticleLayout(view: View)
    }

    interface ArticleActivityView {

        fun setToolbarTitle(title: String)

        fun showSuggestionBottomSheet(paragraph: Int?): Boolean
    }

    interface ArticlePresenter {

        fun setArticleImageHeight(height: Int, objectType: Int)

        fun loadContent()

        fun copyParagraph(paragraph: Paragraph)
    }
}