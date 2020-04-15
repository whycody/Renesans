package pl.renesans.renesans.article

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_article.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Article

class ArticleActivity : AppCompatActivity(), ArticleContract.ArticleView {

    private val imagesList = mutableListOf<ImageView>()
    private lateinit var presenter: ArticleContract.ArticlePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        setSupportActionBar(sourcesToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        sourcesToolbar.navigationIcon?.setColorFilter(ContextCompat.getColor(this,
            android.R.color.white), PorterDuff.Mode.SRC_ATOP)
        imagesList.add(articleImage)
        presenter = ArticlePresenterImpl(applicationContext, this)
        presenter.loadContent()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun getArticleObject(): Article {
        return intent.getSerializableExtra(ARTICLE) as Article
    }

    override fun setTitle(title: String) {
        sourcesToolbar.title = title
    }

    override fun loadBitmapToImage(bitmap: Bitmap, pos: Int) {
        Glide.with(applicationContext).load(bitmap).into(getImageAtPos(pos))
    }

    override fun loadUriToImage(uri: Uri, pos: Int) {
        Glide.with(applicationContext).load(uri).placeholder(getImageAtPos(pos).drawable).into(getImageAtPos(pos))
    }

    private fun getImageAtPos(pos: Int): ImageView{
        return imagesList[pos]
    }

    override fun addViewToArticleLinear(view: View) {
        articleLinear.addView(view)
        if(view is ImageView) imagesList.add(view)
    }

    override fun addViewToHeaderLinear(view: View) {
        headerLinear.addView(view)
    }

    companion object {
        const val ARTICLE = "Article"
    }
}
