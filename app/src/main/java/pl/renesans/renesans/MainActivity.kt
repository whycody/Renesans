package pl.renesans.renesans

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.discover.DiscoverFragment
import pl.renesans.renesans.map.MapFragment
import pl.renesans.renesans.settings.SettingsFragment

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val firestore = FirebaseFirestore.getInstance()
    private var discoverFragment = DiscoverFragment()
    private var mapFragment = MapFragment()
    private val settingsFragment = SettingsFragment()
    private var refreshMapFragment = false
    private var changedOptionOfMapLimit = false
    private var currentItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar as Toolbar)
        mainNav.setOnNavigationItemSelectedListener(this)
        if(savedInstanceState?.getInt("lastTab") != null)
            onNavigationItemSelected(mainNav.menu.getItem(savedInstanceState.getInt("lastTab")))
        else changeFragment(discoverFragment, "discover")
        val realmDaoImpl = RealmDaoImpl(applicationContext)
        realmDaoImpl.refreshRealmDatabase()
        realmDaoImpl.checkRealm()
    }

    private fun saveAllArticles(){
        val articleDao = ArticleDaoImpl()
        for(article in articleDao.getAllArticles())
            firestore.collection("articles").document(article.objectId!![0].toString())
                .collection(article.objectId!![0].toString()).document(article.objectId!!).set(article)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("lastTab", currentItem)
    }

    fun refreshMapFragment(){
        refreshMapFragment = true
    }

    fun changedOptionOfMapLimit(){
        changedOptionOfMapLimit = true
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        return when(p0.itemId) {
            R.id.discover -> {
                currentItem = 0
                changeFragment(discoverFragment, "discover")
                true
            }R.id.map -> {
                currentItem = 1
                changeFragment(mapFragment, "map")
                true
            }R.id.settings -> {
                currentItem = 2
                changeFragment(settingsFragment, "settings")
                true
            }
            else -> false
        }
    }

    private fun changeFragment(fragment: Fragment, tagFragmentName: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val currentFragment = fragmentManager.primaryNavigationFragment
        if (currentFragment != null) fragmentTransaction.hide(currentFragment)
        var fragmentTemp = fragmentManager.findFragmentByTag(tagFragmentName)
        val firstLoadOfFragment = fragmentTemp == null
        if (fragmentTemp == null) {
            fragmentTemp = fragment
            fragmentTransaction.add(R.id.mainFrameLayout, fragmentTemp, tagFragmentName)
        } else fragmentTransaction.show(fragmentTemp)
        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNowAllowingStateLoss()
        if(!firstLoadOfFragment && tagFragmentName == "map") {
            if(refreshMapFragment) mapFragment.reloadMap()
            if(changedOptionOfMapLimit) mapFragment.changedOptionOfMapLimit()
            refreshMapFragment = false
            changedOptionOfMapLimit = false
        }
    }
}