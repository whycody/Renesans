package pl.renesans.renesans

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.article.ArticleDao
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.firebase.FirebaseContract
import pl.renesans.renesans.data.firebase.FirebaseDaoImpl
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.discover.DiscoverFragment
import pl.renesans.renesans.map.ClusterMarker
import pl.renesans.renesans.map.MapFragment
import pl.renesans.renesans.settings.SettingsFragment
import pl.renesans.renesans.settings.SettingsPresenterImpl
import pl.renesans.renesans.toast.ToastHelperImpl

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, FirebaseContract.FirebaseInterractor {

    private val discoverFragment = DiscoverFragment()
    private var mapFragment = MapFragment()
    private val settingsFragment = SettingsFragment()
    private val firebaseDao = FirebaseDaoImpl()
    private var refreshMapFragment = false
    private var refreshDiscoverFragment = false
    private var changedOptionOfMapLimit = false
    private var toastHelper = ToastHelperImpl(this)
    private lateinit var realmDao: RealmContract.RealmDao
    private lateinit var articleDao: ArticleDao
    private var currentItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkBundle(savedInstanceState)
        setContentView(R.layout.activity_main)
        realmDao = RealmDaoImpl(applicationContext)
        articleDao = ArticleDaoImpl(applicationContext)
        setSupportActionBar(mainToolbar as Toolbar)
        mainNav.setOnNavigationItemSelectedListener(this)
        if(savedInstanceState?.getInt(LAST_TAB) != null)
            onNavigationItemSelected(mainNav.menu.getItem(savedInstanceState.getInt(LAST_TAB)))
        else changeFragment(discoverFragment, DISCOVER)
        firebaseDao.refreshArticles()
    }

    private fun checkBundle(savedInstanceState: Bundle?) {
        if(savedInstanceState != null){
            val mapFragmentInManager = supportFragmentManager.findFragmentByTag(MAP)
            if(mapFragmentInManager != null) mapFragment = mapFragmentInManager as MapFragment
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if(requestCode == SettingsPresenterImpl.WRITE_EXTERNAL_STORAGE &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED)
            settingsFragment.writeExternalStoragePermissionGranted()
    }

    fun showSuggestionBottomSheet(article: Article, paragraph: Int?) =
        SuggestionBottomSheetDialog().newInstance(article, paragraph)
            .show(supportFragmentManager, PARAGRAPH)

    fun startArticleActivity(id: String) {
        val intent = Intent(applicationContext, ArticleActivity::class.java)
        intent.putExtra(ArticleActivity.ARTICLE, realmDao.getArticleWithId(id))
        startActivity(intent)
    }

    fun showPhotoArticleOnMap(id: String) {
        mainNav.selectedItemId = R.id.map
        val photoArticle = realmDao.getPhotoArticleWithId(id)
        photoArticle.objectType = ArticleDaoImpl.BOOKMARK_TYPE
        val clusterMarker = ClusterMarker(photoArticle)
        mapFragment.onClusterItemClick(clusterMarker)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(LAST_TAB, currentItem)
    }

    fun refreshMapFragment() {
        refreshMapFragment = true
    }

    fun changedOptionOfMapLimit() {
        changedOptionOfMapLimit = true
    }

    fun refreshDiscoverFragment() {
        refreshDiscoverFragment = true
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        return when(p0.itemId) {
            R.id.discover -> {
                currentItem = 0
                changeFragment(discoverFragment, DISCOVER)
                true
            }R.id.map -> {
                currentItem = 1
                changeFragment(mapFragment, MAP)
                true
            }R.id.settings -> {
                currentItem = 2
                changeFragment(settingsFragment, SETTINGS)
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
        setupFragmentTransaction(fragmentTransaction, fragmentTemp)
        refreshFragments(firstLoadOfFragment, tagFragmentName)
    }

    private fun setupFragmentTransaction(fragmentTransaction: FragmentTransaction, fragmentTemp: Fragment?) {
        with(fragmentTransaction) {
            setPrimaryNavigationFragment(fragmentTemp)
            setReorderingAllowed(true)
            commitNowAllowingStateLoss()
        }
    }

    private fun refreshFragments(firstLoadOfFragment: Boolean, tagFragmentName: String) {
        if(!firstLoadOfFragment && tagFragmentName == MAP) {
            if(refreshMapFragment) mapFragment.reloadMap()
            if(changedOptionOfMapLimit) mapFragment.changedOptionOfMapLimit()
            refreshMapFragment = false
            changedOptionOfMapLimit = false
        }else if(!firstLoadOfFragment && tagFragmentName == DISCOVER) {
            if(refreshDiscoverFragment) {
                discoverFragment.refreshDiscoverFragments()
                refreshDiscoverFragment = false
            }
        }
    }

    override fun onSuccess() = toastHelper.showToast(getString(R.string.suggestions_sent))

    override fun onFail() = toastHelper.showToast(getString(R.string.suggestions_fail))

    companion object {
        const val DISCOVER = "discover"
        const val MAP = "map"
        const val SETTINGS = "settings"
        const val LAST_TAB = "last tab"
        const val PARAGRAPH = "paragraph"
    }

}