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
import pl.renesans.renesans.data.ArticlesList
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
    private lateinit var fragMan: FragmentManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover, container, false)
        realmDao = RealmDaoImpl(activity!!.applicationContext, this)
        refreshLayout = view.refreshLayout
        discoverLayout = view.discoverLayout
        toastHelper = ToastHelperImpl(activity!!)
        fragMan = fragmentManager!!
        if(savedInstanceState == null) addFragmentsToDiscoverLayout()
        view.clickableSearchView.setOnClickListener(this)
        realmDao.refreshRealmDatabase()
        view.refreshLayout.setOnRefreshListener { realmDao.refreshRealmDatabase() }
        return view
    }

    private fun addFragmentsToDiscoverLayout(){
        val fragTransaction: FragmentTransaction = fragMan.beginTransaction()
        addDiscoverRecyclerFragmentsToLayout(fragTransaction)
        fragTransaction.commit()
    }

    private fun addDiscoverRecyclerFragmentsToLayout(fragTransaction: FragmentTransaction) {
        realmDao.getArticlesLists().forEach {
            if(it.type == RealmDaoImpl.ARTICLE)
                addDiscoverRecyclerFragment(it, fragTransaction)
        }
    }

    private fun addDiscoverRecyclerFragment(articlesList: ArticlesList,
                                            fragTransaction: FragmentTransaction) {
        val frag =
            DiscoverRecyclerFragment().newInstance(articlesList.objectType!!, articlesList.id!!)
        fragTransaction.add(discoverLayout.id, frag, "${articlesList.id}Frag")
    }

    override fun downloadSuccessful() {
        removeAllDiscoverFragmentsFromLayout()
        (activity as MainActivity).refreshMapFragment()
        addFragmentsToDiscoverLayout()
        showInformationAboutDbUpdatedSuccessfully()
        refreshLayout.isRefreshing = false
    }

    private fun showInformationAboutDbUpdatedSuccessfully() =
        toastHelper.showToast(getString(R.string.updated_database_successfully))

    private fun removeAllDiscoverFragmentsFromLayout() {
        val fragTransaction: FragmentTransaction = fragMan!!.beginTransaction()
        for(fragment in fragMan.fragments)
            if(fragment is DiscoverRecyclerFragment)
                fragTransaction.remove(fragment)
        fragTransaction.commit()
    }

    fun refreshDiscoverFragments() {
        val fragMan: FragmentManager? = fragmentManager
        for(fragment in fragMan!!.fragments)
            if(fragment is DiscoverRecyclerFragment)
                refreshFragment(fragment)
    }

    private fun refreshFragment(fragment: DiscoverRecyclerFragment) = fragment.notifyDataSetChanged()

    override fun downloadFailure(connectionProblem: Boolean) {
        if(!refreshLayout.isRefreshing) return
        showInformationAboutTheProblem(connectionProblem)
        refreshLayout.isRefreshing = false
    }

    private fun showInformationAboutTheProblem(connectionProblem: Boolean) {
        if(!connectionProblem) toastHelper.showToast(activity!!.getString(R.string.suggestions_fail))
        else toastHelper.showToast(getString(R.string.you_are_offline))
    }

    override fun startedLoading() {
        refreshLayout.isRefreshing = true
    }

    override fun downloadedProgress(percentages: Int) { }

    override fun databaseIsUpToDate() {
        if(!refreshLayout.isRefreshing) return
        showInformationAboutDatabaseIsUpToDate()
        refreshLayout.isRefreshing = false
    }

    private fun showInformationAboutDatabaseIsUpToDate() =
        toastHelper.showToast(getString(R.string.database_up_to_date))

    override fun onClick(v: View?) {
        startActivity(Intent(context!!.applicationContext, SearchActivity::class.java))
        activity?.overridePendingTransition(0, 0)
    }
}