package pl.renesans.renesans.discover

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

class DiscoverFragment : Fragment() {

    private lateinit var discoverLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover, container, false)
        discoverLayout = view.findViewById(R.id.discoverLayout)
        addFragmentsToDiscoverLayout()
        return view
    }

    private fun addFragmentsToDiscoverLayout(){
        val fragMan: FragmentManager? = fragmentManager
        val fragTransaction: FragmentTransaction = fragMan!!.beginTransaction()
        val peopleFrag = DiscoverRecyclerFragment(DiscoverRecyclerFragment.PEOPLE)
        val artsFrag = DiscoverRecyclerFragment(DiscoverRecyclerFragment.ARTS)
        val eventsFrag = DiscoverRecyclerFragment(DiscoverRecyclerFragment.EVENTS)
        val otherErasFrag = DiscoverRecyclerFragment(DiscoverRecyclerFragment.OTHER_ERAS)
        fragTransaction.add(discoverLayout.id, peopleFrag, "peopleFrag")
        fragTransaction.add(discoverLayout.id, artsFrag, "artsFrag")
        fragTransaction.add(discoverLayout.id, eventsFrag, "eventsFrag")
        fragTransaction.add(discoverLayout.id, otherErasFrag, "otherErasFrag")
        fragTransaction.commit()
    }
}
