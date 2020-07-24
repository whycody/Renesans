package pl.renesans.renesans.discover

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_discover.view.*
import pl.renesans.renesans.MainActivity
import pl.renesans.renesans.R
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment
import pl.renesans.renesans.search.SearchActivity

class DiscoverFragment : Fragment(), RealmContract.RealmInterractor {

    private lateinit var discoverLayout: LinearLayout
    private lateinit var realmDao: RealmContract.RealmDao
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover, container, false)
        refreshLayout = view.refreshLayout
        discoverLayout = view.discoverLayout
        realmDao = RealmDaoImpl(activity!!.applicationContext, this)
        realmDao.onCreate()
        view.clickableSearchView.setOnClickListener{
            startActivity(Intent(context!!.applicationContext, SearchActivity::class.java))
            activity?.overridePendingTransition(0, 0)
        }
        if(savedInstanceState == null) addFragmentsToDiscoverLayout()
        realmDao.refreshRealmDatabase()
        view.refreshLayout.setOnRefreshListener { realmDao.refreshRealmDatabase() }
        return view
    }

    private fun addFragmentsToDiscoverLayout(){
        val fragMan: FragmentManager? = fragmentManager
        val fragTransaction: FragmentTransaction = fragMan!!.beginTransaction()
        realmDao.getArticlesLists().forEach{
            val frag = DiscoverRecyclerFragment().newInstance(it.objectType!!, it.id!!)
            if(it.type == RealmDaoImpl.ARTICLE)
                fragTransaction.add(discoverLayout.id, frag, "${it.id}Frag")
        }
        fragTransaction.commit()
    }

    override fun downloadSuccessful() {
        val fragMan: FragmentManager? = fragmentManager
        val fragTransaction: FragmentTransaction = fragMan!!.beginTransaction()
        for(fragment in fragMan.fragments)
            if(fragment is DiscoverRecyclerFragment) fragTransaction.remove(fragment)
        fragTransaction.commit()
        (activity as MainActivity).refreshMapFragment()
        addFragmentsToDiscoverLayout()
        showToast(getString(R.string.updated_database_successfully))
        refreshLayout.isRefreshing = false
    }

    fun refreshFragment(){
        val fragMan: FragmentManager? = fragmentManager
        for(fragment in fragMan!!.fragments)
            if(fragment is DiscoverRecyclerFragment)
                fragment.notifyDataSetChanged()
    }

    override fun downloadFailure(connectionProblem: Boolean) {
        if(!refreshLayout.isRefreshing) return
        if(!connectionProblem) showToast(activity!!.getString(R.string.suggestions_fail))
        else showToast(getString(R.string.you_are_offline))
        refreshLayout.isRefreshing = false
    }

    override fun startedLoading() {
        refreshLayout.isRefreshing = true
    }

    override fun downloadedProgress(percentages: Int) {

    }

    override fun databaseIsUpToDate() {
        if(refreshLayout.isRefreshing)
            showToast(getString(R.string.database_up_to_date))
        refreshLayout.isRefreshing = false
    }

    private fun showToast(text: String){
        val view = activity?.layoutInflater?.inflate(R.layout.toast_suggestion,
            activity?.findViewById(R.id.toastView))
        view?.findViewById<TextView>(R.id.toastText)?.text = text
        val toast = Toast(activity?.applicationContext)
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        toast.view = view
        toast.show()
    }
}