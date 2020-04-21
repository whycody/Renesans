package pl.renesans.renesans.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search.*
import pl.renesans.renesans.R
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.search.recycler.SearchContract
import pl.renesans.renesans.search.recycler.SearchPresenterImpl
import pl.renesans.renesans.search.recycler.SearchRecyclerAdapter

class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener, SearchContract.SearchView,
    MenuItem.OnActionExpandListener {

    private lateinit var presenter: SearchContract.SearchPresenter
    private lateinit var adapter: SearchRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(searchToolbar)
        presenter = SearchPresenterImpl(applicationContext, this)
        presenter.onCreate()
        adapter = SearchRecyclerAdapter(applicationContext, presenter)
        searchRecycler.adapter = adapter
        searchRecycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu?.findItem(R.id.searchIcon)
        val searchView = menuItem?.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = menuItem.title
        searchView.setOnQueryTextListener(this)
        menuItem.setOnActionExpandListener(this)
        menuItem.expandActionView()
        return true
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        adapter.filter.filter(p0)
        return false
    }

    override fun startArticleActivity(article: Article) {
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra(ArticleActivity.ARTICLE, article)
        startActivity(intent)
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        finish()
        overridePendingTransition(0, 0)
        return true
    }
}
