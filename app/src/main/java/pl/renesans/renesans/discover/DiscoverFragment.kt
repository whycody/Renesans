package pl.renesans.renesans.discover

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import pl.renesans.renesans.R
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment
import pl.renesans.renesans.search.SearchActivity

class DiscoverFragment : Fragment() {

    private lateinit var discoverLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover, container, false)
        discoverLayout = view.findViewById(R.id.discoverLayout)
        view.findViewById<View>(R.id.clickableSearchView).setOnClickListener{
            startActivity(Intent(context!!.applicationContext, SearchActivity::class.java))
            activity?.overridePendingTransition(0, 0)
        }
        if(savedInstanceState == null) addFragmentsToDiscoverLayout()
        return view
    }

    private fun addFragmentsToDiscoverLayout(){
        val fragMan: FragmentManager? = fragmentManager
        val fragTransaction: FragmentTransaction = fragMan!!.beginTransaction()
        val peopleFrag = DiscoverRecyclerFragment().newInstance(DiscoverRecyclerFragment.PEOPLE)
        val artsFrag = DiscoverRecyclerFragment().newInstance(DiscoverRecyclerFragment.ARTS)
        val eventsFrag = DiscoverRecyclerFragment().newInstance(DiscoverRecyclerFragment.EVENTS)
        val otherErasFrag = DiscoverRecyclerFragment().newInstance(DiscoverRecyclerFragment.OTHER_ERAS)
        fragTransaction.add(discoverLayout.id, peopleFrag, "peopleFrag")
        fragTransaction.add(discoverLayout.id, artsFrag, "artsFrag")
        fragTransaction.add(discoverLayout.id, eventsFrag, "eventsFrag")
        fragTransaction.add(discoverLayout.id, otherErasFrag, "otherErasFrag")
        fragTransaction.commit()
    }
}