package pl.renesans.renesans.discover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import pl.renesans.renesans.R

class DiscoverContentFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover_content, container, false)
        val discoverFragment = DiscoverFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.discoverContentFrame, discoverFragment)
        transaction.commit()
        return view
    }

}
