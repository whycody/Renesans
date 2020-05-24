package pl.renesans.renesans.discover.recycler

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class DiscoverRecyclerFragment: Fragment() {

    private lateinit var discoverRecycler: RecyclerView
    private lateinit var discoverTitle: TextView
    private lateinit var presenter: DiscoverRecyclerPresenter
    private lateinit var adapter: DiscoverRecyclerAdapter
    private var objectType = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover_recycler, container, false)
        discoverRecycler = view.findViewById(R.id.discoverRecycler)
        discoverTitle = view.findViewById(R.id.discoverTitle)
        presenter = DiscoverRecyclerPresenterImpl(objectType, context!!)
        presenter.onCreate(objectType)
        adapter = DiscoverRecyclerAdapter(context!!, presenter)
        discoverRecycler.adapter = adapter
        discoverRecycler.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        discoverRecycler.addItemDecoration(DiscoverRecyclerDecoration(context!!))
        setRecyclerTitle()
        return view
    }

    fun setObjectType(objectType: Int){
        this.objectType = objectType
    }

    private fun setRecyclerTitle(){
        when (objectType){
            PEOPLE -> discoverTitle.text = getString(R.string.important_people)
            ARTS -> discoverTitle.text = getString(R.string.important_arts)
            EVENTS -> discoverTitle.text = getString(R.string.important_events)
            OTHER_ERAS -> discoverTitle.text = getString(R.string.other_eras)
        }
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
