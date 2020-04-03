package pl.renesans.renesans.discover.recycler.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R
import pl.renesans.renesans.discover.recycler.*

class DiscoverRecyclerFragment(val objectType: Int) : Fragment(),
    DiscoverRecyclerView {

    private lateinit var discoverRecycler: RecyclerView
    private lateinit var discoverTitle: TextView
    private lateinit var presenter: DiscoverRecyclerPresenter
    private lateinit var adapter: DiscoverRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover_recycler, container, false)
        discoverRecycler = view.findViewById(R.id.discoverRecycler)
        discoverTitle = view.findViewById(R.id.discoverTitle)
        presenter = DiscoverRecyclerPresenterImpl(objectType, activity!!, this)
        presenter.onCreate(objectType)
        adapter = DiscoverRecyclerAdapter(activity!!, presenter)
        discoverRecycler.adapter = adapter
        discoverRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        discoverRecycler.addItemDecoration(DiscoverRecyclerDecoration(activity!!))
        setRecyclerTitle()
        return view
    }

    private fun setRecyclerTitle(){
        when (objectType){
            PEOPLE -> discoverTitle.text = getString(R.string.important_people)
            ARTS -> discoverTitle.text = getString(R.string.important_arts)
            EVENTS -> discoverTitle.text = getString(R.string.important_events)
            OTHER_ERAS -> discoverTitle.text = getString(R.string.other_eras)
        }
    }

    override fun getDiscoverHolderAtPosition(pos: Int): DiscoverRowHolder? {
        Log.d("MOJTAG", "Pozycja: $pos, ilość: ${presenter.getItemCount()}, nullostwo: " +
                "${discoverRecycler.findViewHolderForAdapterPosition(pos)==null}")
        if(discoverRecycler.findViewHolderForAdapterPosition(pos)!=null)
        return discoverRecycler.findViewHolderForAdapterPosition(pos) as DiscoverRowHolder
        else return null
    }

    companion object{
        const val PEOPLE = 0
        const val ARTS = 1
        const val EVENTS = 2
        const val OTHER_ERAS = 3
    }

}
