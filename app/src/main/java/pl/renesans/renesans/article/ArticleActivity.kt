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
import pl.renesans.renesans.data.firebase.FirebaseContract
import pl.renesans.renesans.toast.ToastHelperImpl

class ArticleActivity : AppCompatActivity(), ArticleContract.ArticleActivityView, FirebaseContract.FirebaseInterractor {

    private lateinit var articleFragment: ArticleFragment
    private val toastHelper = ToastHelperImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        setSupportActionBar(articleToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        articleToolbar.navigationIcon?.setColorFilter(ContextCompat.getColor(this,
            android.R.color.white), PorterDuff.Mode.SRC_ATOP)
        setFragment()
    }

    private fun setFragment() {
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
           showSuggestionBottomSheet(SuggestionBottomSheetDialog.CONTENT_OF_ARTICLE)
           true
       }else MenuItem.OnMenuItemClickListener {
           showSuggestionBottomSheet(index)
           true
       }
   }

    override fun showSuggestionBottomSheet(paragraph: Int?): Boolean {
        SuggestionBottomSheetDialog().newInstance(articleFragment.getArticleObject(), paragraph)
            .show(supportFragmentManager, "Paragraph")
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun setToolbarTitle(title: String) {
        articleToolbar.title = title
    }

    override fun onSuccess() = toastHelper.showToast(getString(R.string.suggestions_sent))

    override fun onFail() = toastHelper.showToast(getString(R.string.suggestions_fail))

    companion object {
        const val ARTICLE = "Article"
        const val PHOTO_ARTICLE = "Photo Article"
    }
}
