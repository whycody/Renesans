package pl.renesans.renesans.sources

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_sources.*
import kotlinx.android.synthetic.main.activity_sources.sourcesToolbar
import pl.renesans.renesans.R
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.Article

class SourcesActivity : AppCompatActivity(), SourcesContract.SourcesView {

    private lateinit var presenter: SourcesContract.SourcesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sources)
        setSupportActionBar(sourcesToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        sourcesToolbar.navigationIcon?.setColorFilter(ContextCompat.getColor(this,
            android.R.color.white), PorterDuff.Mode.SRC_ATOP)
        presenter = SourcesPresenterImpl(this, this)
        presenter.onCreate()
        val adapter = SourcesRecyclerAdapter(this, presenter)
        sourcesRecycler.layoutManager = LinearLayoutManager(this)
        sourcesRecycler.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun getArticleObject(): Article {
        return intent.getSerializableExtra(ArticleActivity.ARTICLE) as Article
    }

    override fun startUrlActivity(url: String) {
        val uriUrl = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
        overridePendingTransition(0, 0)
    }
}
