package pl.renesans.renesans.utility.article.views

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Photo
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerDecoration

class ArticleViewsUtilityImpl(private val context: Context): ArticleViewsUtility {

    private val articleMargin: Int = context.resources.getDimension(R.dimen.articleMargin).toInt()
    private val articleBigUpMargin =
        context.resources.getDimension(R.dimen.articleBigUpMargin).toInt()
    private val articleSmallUpMargin =
        context.resources.getDimension(R.dimen.articleSmallUpMargin).toInt()

    override fun getHeaderTitleView(text: String?): TextView {
        with(TextView(context)) {
            this.text = text
            alpha = .8f
            setPadding(0, 0, 0, 10)
            TextViewCompat.setTextAppearance(this, R.style.ArticleHeaderTitleTextViewStyle)
            return this
        }
    }

    override fun getHeaderContentView(text: String?): TextView {
        with(TextView(context)) {
            this.text = text
            alpha = .8f
            setLineSpacing(10f, 1f)
            TextViewCompat.setTextAppearance(this, R.style.ArticleHeaderContentTextViewStyle)
            return this
        }
    }

    override fun getParagraphTitleView(text: String?): TextView {
        with(TextView(context)) {
            this.text = text
            setPadding(articleMargin, articleBigUpMargin, articleMargin, 0)
            TextViewCompat.setTextAppearance(this, R.style.ArticleContentTitleTextViewStyle)
            return this
        }
    }

    override fun getParagraphContentView(text: String?): TextView {
        with(TextView(context)) {
            this.text = text
            setLineSpacing(25f, 1f)
            val linearParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            linearParams.setMargins(articleMargin, 0, articleMargin, 0)
            layoutParams = linearParams
            TextViewCompat.setTextAppearance(this, R.style.ArticleContentContentTextViewStyle)
            return this
        }
    }

    override fun getInvisibleView(bigMargin: Boolean): View {
        with(View(context)) {
            val linearParams = LinearLayout.LayoutParams(1, 1)
            linearParams.setMargins(articleMargin, articleSmallUpMargin, articleMargin, 0)
            if(bigMargin) linearParams.topMargin = articleBigUpMargin
            layoutParams = linearParams
            setBackgroundColor(Color.TRANSPARENT)
            return this
        }
    }

    override fun getParagraphImageView(photo: Photo): ImageView {
        with(ImageView(context)) {
            adjustViewBounds = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            setPadding(0, articleBigUpMargin, 0, 0)
            return this
        }
    }

    override fun getParagraphImageDescriptionView(text: String?): TextView {
        with(TextView(context)) {
            this.text = text
            setLineSpacing(10f, 1f)
            setPadding(articleMargin, articleSmallUpMargin, articleMargin, 0)
            TextViewCompat.setTextAppearance(this, R.style.ArticleHeaderContentTextViewStyle)
            return this
        }
    }

    override fun getRelatedRecyclerView(): RecyclerView {
        val recyclerView = RecyclerView(context)
        recyclerView.addItemDecoration(DiscoverRecyclerDecoration(context))
        recyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setPadding(0, articleSmallUpMargin, 0, 0)
        return recyclerView
    }
}