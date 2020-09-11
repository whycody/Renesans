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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_discover.view.*
import pl.renesans.renesans.MainActivity
import pl.renesans.renesans.R
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment
import pl.renesans.renesans.search.SearchActivity
import pl.renesans.renesans.toast.ToastHelper
import pl.renesans.renesans.toast.ToastHelperImpl

class DiscoverFragment : Fragment(), RealmContract.RealmInterractor, View.OnClickListener {

    private lateinit var realmDao: RealmContract.RealmDao
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var discoverLayout: LinearLayout
    private lateinit var toastHelper: ToastHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover, container, false)
        realmDao = RealmDaoImpl(activity!!.applicationContext, this)
        refreshLayout = view.refreshLayout
        discoverLayout = view.discoverLayout
        toastHelper = ToastHelperImpl(activity!!)
        if(savedInstanceState == null) addFragmentsToDiscoverLayout()
        view.clickableSearchView.setOnClickListener(this)
        realmDao.refreshRealmDatabase()
        view.refreshLayout.setOnRefreshListener { realmDao.refreshRealmDatabase() }
        return view
    }

    private fun addFragmentsToDiscoverLayout(){
        val fragMan: FragmentManager? = fragmentManager
        val fragTransaction: FragmentTransaction = fragMan!!.beginTransaction()
        addDiscoverRecyclerFragmentsToLayout(fragTransaction)
        fragTransaction.commit()
    }

    private fun addDiscoverRecyclerFragmentsToLayout(fragTransaction: FragmentTransaction) {
        realmDao.getArticlesLists().forEach{
            val frag = DiscoverRecyclerFragment().newInstance(it.objectType!!, it.id!!)
            if(it.type == RealmDaoImpl.ARTICLE)
                fragTransaction.add(discoverLayout.id, frag, "${it.id}Frag")
        }
    }

    override fun downloadSuccessful() {
        val fragMan: FragmentManager? = fragmentManager
        val fragTransaction: FragmentTransaction = fragMan!!.beginTransaction()
        for(fragment in fragMan.fragments)
            if(fragment is DiscoverRecyclerFragment) fragTransaction.remove(fragment)
        fragTransaction.commit()
        (activity as MainActivity).refreshMapFragment()
        addFragmentsToDiscoverLayout()
        toastHelper.showToast(getString(R.string.updated_database_successfully))
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
        if(!connectionProblem) toastHelper.showToast(activity!!.getString(R.string.suggestions_fail))
        else toastHelper.showToast(getString(R.string.you_are_offline))
        refreshLayout.isRefreshing = false
    }

    override fun startedLoading() {
        refreshLayout.isRefreshing = true
    }

    override fun downloadedProgress(percentages: Int) { }

    override fun databaseIsUpToDate() {
        if(refreshLayout.isRefreshing)
            toastHelper.showToast(getString(R.string.database_up_to_date))
        refreshLayout.isRefreshing = false
    }

    override fun onClick(v: View?) {
        startActivity(Intent(context!!.applicationContext, SearchActivity::class.java))
        activity?.overridePendingTransition(0, 0)
    }
}