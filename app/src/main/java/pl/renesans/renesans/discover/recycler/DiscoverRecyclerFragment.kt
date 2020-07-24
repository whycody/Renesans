package pl.renesans.renesans.discover.recycler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class DiscoverRecyclerFragment: Fragment() {

    private lateinit var discoverRecycler: RecyclerView
    private lateinit var discoverTitle: TextView
    private lateinit var presenter: DiscoverRecyclerPresenter
    private lateinit var adapter: DiscoverRecyclerAdapter
    private var objectType = 0
    private var articlesListId = "P"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover_recycler, container, false)
        if(arguments!=null) {
            objectType = arguments!!.getInt("objectType")
            articlesListId = arguments!!.getString("articlesListId")
        }
        discoverRecycler = view.findViewById(R.id.discoverRecycler)
        discoverTitle = view.findViewById(R.id.discoverTitle)
        presenter = DiscoverRecyclerPresenterImpl(objectType, context!!)
        presenter.onCreate(articlesListId)
        adapter = DiscoverRecyclerAdapter(context!!, presenter)
        discoverRecycler.adapter = adapter
        discoverRecycler.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        discoverRecycler.addItemDecoration(DiscoverRecyclerDecoration(context!!))
        discoverTitle.text = presenter.getArticlesListTitle()
        return view
    }

    fun newInstance(objectType: Int, articlesListId: String): DiscoverRecyclerFragment {
        val args = Bundle()
        args.putInt("objectType", objectType)
        args.putString("articlesListId", articlesListId)
        val discoverFragment = DiscoverRecyclerFragment()
        discoverFragment.arguments = args
        return discoverFragment
    }

    fun notifyDataSetChanged(){
        adapter.notifyDataSetChanged()
        Log.d("MOJTAG", "Notified")
    }

    companion object{
        const val PEOPLE = 0
        const val ARTS = 1
        const val EVENTS = 2
        const val OTHER_ERAS = 3
        const val SOURCES = 4
        const val PHOTOS = 5
        const val TOUR = 6
    }

}
