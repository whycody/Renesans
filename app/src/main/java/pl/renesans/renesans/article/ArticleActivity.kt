package pl.renesans.renesans.article

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_article.*
import pl.renesans.renesans.R
import pl.renesans.renesans.SuggestionBottomSheetDialog
import pl.renesans.renesans.data.Article

class ArticleActivity : AppCompatActivity(), ArticleContract.ArticleActivityView {

    private lateinit var article: Article
    private lateinit var articleFragment: ArticleFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        setSupportActionBar(articleToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        article = getArticleObject()
        articleToolbar.navigationIcon?.setColorFilter(ContextCompat.getColor(this,
            android.R.color.white), PorterDuff.Mode.SRC_ATOP)
        setFragment()
    }

    private fun getArticleObject(): Article {
        return intent.getSerializableExtra(ARTICLE) as Article
    }

    private fun setFragment(){
        articleFragment = ArticleFragment(articleActivityView = this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.articleFrame, articleFragment)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val popup = PopupMenu(this, articleToolbar)
        popup.menuInflater.inflate(R.menu.article_popup_menu, menu)
        val subMenu = menu!!.getItem(0).subMenu
        subMenu.add(getString(R.string.content_of_article))
            .setOnMenuItemClickListener(getOnMenuItemClickListener(0))
        subMenu.add(getString(R.string.new_paragraph))
            .setOnMenuItemClickListener(getOnMenuItemClickListener())
        return true
    }

   private fun getOnMenuItemClickListener(index: Int? = null): MenuItem.OnMenuItemClickListener {
       return if (index == 0) MenuItem.OnMenuItemClickListener {
           SuggestionBottomSheetDialog().newInstance(article,
               SuggestionBottomSheetDialog.CONTENT_OF_ARTICLE,
               articleFragment.getFirebaseInterractor()).show(supportFragmentManager, "Content")
           true
       }else MenuItem.OnMenuItemClickListener {
           SuggestionBottomSheetDialog().newInstance(article, index,
               articleFragment.getFirebaseInterractor()).show(supportFragmentManager, "Paragraph")
           true
       }
   }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun setTitle(title: String) {
        articleToolbar.title = title
    }

    companion object {
        const val ARTICLE = "Article"
    }
}
