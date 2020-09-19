package pl.renesans.renesans.search

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search.*
import pl.renesans.renesans.R
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.search.recycler.SearchContract
import pl.renesans.renesans.search.recycler.SearchPresenterImpl
import pl.renesans.renesans.search.recycler.SearchRecyclerAdapter

class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    SearchContract.SearchView,
    MenuItem.OnActionExpandListener, ImageDaoContract.ImageDaoInterractor {

    private lateinit var presenter: SearchPresenterImpl
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
        val imageDao = ImageDaoImpl(applicationContext, this)
        presenter = SearchPresenterImpl(realmDao, imageDao, this)
        adapter = SearchRecyclerAdapter(presenter)
        layoutManager = LinearLayoutManager(this)
        searchRecycler.adapter = adapter
        searchRecycler.layoutManager = layoutManager
    }

    private fun checkBundle(savedInstanceState: Bundle?) {
        lastSearchText = savedInstanceState?.getString(SEARCH_TEXT, lastSearchText)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchView?.query.toString())
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
        val searchMenuItem = menu?.findItem(R.id.searchIcon)!!
        setupSearchMenuItem(searchMenuItem)
        setupSearchView(searchView!!, this.title.toString())
        return true
    }

    private fun setupSearchMenuItem(searchMenuItem: MenuItem) {
        with(searchMenuItem) {
            setOnActionExpandListener(this@SearchActivity)
            expandActionView()
            searchView = actionView as SearchView
            setupSearchView(searchView!!, this.title.toString())
        }
    }

    private fun setupSearchView(searchView: SearchView, queryHint: String) {
        with(searchView) {
            maxWidth = Integer.MAX_VALUE
            this.queryHint = queryHint
            setOnQueryTextListener(this@SearchActivity)
            setQuery(lastSearchText, false)
        }
    }

    override fun onQueryTextSubmit(p0: String?) = false

    override fun onQueryTextChange(p0: String?): Boolean {
        adapter.filter.filter(p0)
        lastFilter = p0
        return false
    }

    override fun startArticleActivity(id: String) {
        val typeOfArticle = realmDao.getTypeOfArticle(id)
        val article = getArticleFromId(id, typeOfArticle)
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra(typeOfArticle, article)
        startActivity(intent)
    }

    private fun getArticleFromId(id: String, typeOfArticle: String) =
        if(typeOfArticle == ArticleActivity.ARTICLE)
            realmDao.getArticleWithId(id)
        else realmDao.getPhotoArticleWithId(id)

    override fun getSearchDefaultDrawable() =
        ContextCompat.getDrawable(applicationContext, R.drawable.sh_search_recycler_row)!!

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

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) =
        presenter.loadPhotoFromUri(photoUri, pos)

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) =
        presenter.loadPhotoFromBitmap(photoBitmap, pos)

    companion object {
        const val SEARCH_TEXT = "searchText"
    }
}
