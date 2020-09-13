package pl.renesans.renesans.discover.recycler

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.data.realm.RealmDaoImpl

class DiscoverRecyclerFragment: Fragment(), DiscoverContract.DiscoverView {

    private lateinit var discoverRecycler: RecyclerView
    private lateinit var discoverTitle: TextView
    private lateinit var presenter: DiscoverRecyclerPresenterImpl
    private lateinit var imageDao: ImageDaoContract.ImageDao
    private lateinit var discoverAdapter: DiscoverRecyclerAdapter
    private lateinit var articlesListId: String
    private var objectType = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover_recycler, container, false)
        if(arguments!=null) getVariablesFromArguments()
        discoverRecycler = view.findViewById(R.id.discoverRecycler)
        discoverTitle = view.findViewById(R.id.discoverTitle)
        presenter = DiscoverRecyclerPresenterImpl(articlesListId, RealmDaoImpl(context!!), this)
        imageDao = ImageDaoImpl(context!!.applicationContext, presenter)
        discoverAdapter = DiscoverRecyclerAdapter(context!!, presenter)
        setupDiscoverRecycler(discoverAdapter)
        return view
    }

    private fun setupDiscoverRecycler(discoverAdapter: DiscoverRecyclerAdapter) {
        with(discoverRecycler) {
            adapter = discoverAdapter
            layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(DiscoverRecyclerDecoration(context!!))
        }
    }

    private fun getVariablesFromArguments() {
        objectType = arguments!!.getInt(OBJECT_TYPE)
        articlesListId = arguments!!.getString(ARTICLES_LIST_ID)!!
    }

    override fun setDiscoverTitle(title: String) {
        discoverTitle.text = title
    }

    override fun newInstance(objectType: Int, articlesListId: String): DiscoverRecyclerFragment {
        val args = Bundle()
        args.putInt(OBJECT_TYPE, objectType)
        args.putString(ARTICLES_LIST_ID, articlesListId)
        val discoverFragment = DiscoverRecyclerFragment()
        discoverFragment.arguments = args
        return discoverFragment
    }

    override fun startArticleActivity(article: Article) {
        val intent = Intent(context, ArticleActivity::class.java)
        intent.putExtra(ArticleActivity.ARTICLE, article)
        startActivity(intent)
    }

    override fun getObjectType() = objectType

    override fun loadPhoto(pos: Int, id: String) = imageDao.loadPhoto(pos, id)

    override fun notifyDataSetChanged() = discoverAdapter.notifyDataSetChanged()

    companion object{
        const val PEOPLE = 0
        const val ARTS = 1
        const val EVENTS = 2
        const val OTHER_ERAS = 3
        const val SOURCES = 4
        const val PHOTOS = 5
        const val TOUR = 6
        const val MAP = 7
        const val OBJECT_TYPE = "objectType"
        const val ARTICLES_LIST_ID = "articlesListId"
    }

}
