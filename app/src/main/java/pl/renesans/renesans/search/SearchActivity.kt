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
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.search.recycler.SearchContract
import pl.renesans.renesans.search.recycler.SearchPresenterImpl
import pl.renesans.renesans.search.recycler.SearchRecyclerAdapter

class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener, SearchContract.SearchView,
    MenuItem.OnActionExpandListener {

    private lateinit var presenter: SearchContract.SearchPresenter
    private lateinit var adapter: SearchRecyclerAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var realmDao: RealmContract.RealmDao
    private var lastFilter: String? = null
    private var searchView: SearchView? = null
    private var lastSearchText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(searchToolbar)
        checkBundle(savedInstanceState)
        realmDao = RealmDaoImpl(applicationContext)
        presenter = SearchPresenterImpl(applicationContext, this)
        presenter.onCreate()
        adapter = SearchRecyclerAdapter(applicationContext, presenter)
        layoutManager = LinearLayoutManager(this)
        searchRecycler.adapter = adapter
        searchRecycler.layoutManager = layoutManager
    }

    private fun checkBundle(savedInstanceState: Bundle?){
        if(savedInstanceState?.getString("searchText") != null)
            lastSearchText = savedInstanceState.getString("searchText")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchText", searchView?.query.toString())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        adapter.filter.filter(lastFilter)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu?.findItem(R.id.searchIcon)
        searchView = menuItem?.actionView as SearchView
        searchView?.maxWidth = Integer.MAX_VALUE
        searchView?.queryHint = menuItem.title
        searchView?.setOnQueryTextListener(this)
        menuItem.setOnActionExpandListener(this)
        menuItem.expandActionView()
        searchView?.setQuery(lastSearchText, false)
        return true
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        adapter.filter.filter(p0)
        lastFilter = p0
        return false
    }

    override fun startArticleActivity(id: String) {
        val typeOfArticle = realmDao.getTypeOfArticle(id)
        val article =
            if(typeOfArticle == ArticleActivity.ARTICLE)
                realmDao.getArticleWithId(id)
            else realmDao.getPhotoArticleWithId(id)
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra(typeOfArticle, article)
        startActivity(intent)
    }

    override fun holderIsVisible(pos: Int): Boolean {
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
        return pos in firstVisibleItemPosition..lastVisibleItem
    }

    override fun viewDeletedAtPos(pos: Int) {
        adapter.notifyItemRemoved(pos)
        adapter.notifyItemRangeChanged(pos, adapter.itemCount)
    }

    override fun onMenuItemActionExpand(item: MenuItem?) = true

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        finish()
        overridePendingTransition(0, 0)
        return true
    }
}
