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
import pl.renesans.renesans.data.realm.RealmDaoImpl
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
        val realmDao = RealmDaoImpl(activity!!.applicationContext)
        realmDao.onCreate()
        realmDao.getArticlesLists().forEach{
            val frag = DiscoverRecyclerFragment().newInstance(it.objectType!!, it.id!!)
            if(it.type == RealmDaoImpl.ARTICLE)
                fragTransaction.add(discoverLayout.id, frag, "${it.id}Frag")
        }
        fragTransaction.commit()
    }
}