package pl.renesans.renesans

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.firebase.FirebaseContract
import pl.renesans.renesans.data.firebase.FirebaseDaoImpl
import pl.renesans.renesans.discover.DiscoverFragment
import pl.renesans.renesans.map.MapFragment
import pl.renesans.renesans.settings.SettingsFragment
import pl.renesans.renesans.settings.SettingsPresenterImpl
import pl.renesans.renesans.toast.ToastHelperImpl

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, FirebaseContract.FirebaseInterractor {

    private var discoverFragment = DiscoverFragment()
    private var mapFragment = MapFragment()
    private val settingsFragment = SettingsFragment()
    private val firebaseDao = FirebaseDaoImpl()
    private var refreshMapFragment = false
    private var refreshDiscoverFragment = false
    private var changedOptionOfMapLimit = false
    private var toastHelper = ToastHelperImpl(this)
    private var currentItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar as Toolbar)
        mainNav.setOnNavigationItemSelectedListener(this)
        if(savedInstanceState?.getInt("lastTab") != null)
            onNavigationItemSelected(mainNav.menu.getItem(savedInstanceState.getInt("lastTab")))
        else changeFragment(discoverFragment, "discover")
        firebaseDao.refreshArticles()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if(requestCode == SettingsPresenterImpl.WRITE_EXTERNAL_STORAGE &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED)
            settingsFragment.writeExternalStoragePermissionGranted()
    }

    fun showSuggestionBottomSheet(article: Article, paragraph: Int?) =
        SuggestionBottomSheetDialog().newInstance(article, paragraph)
            .show(supportFragmentManager, "Paragraph")

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("lastTab", currentItem)
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
        }else if(!firstLoadOfFragment && tagFragmentName == "discover"){
            if(refreshDiscoverFragment){
                discoverFragment.refreshFragment()
                refreshDiscoverFragment = false
            }
        }
    }

    override fun onSuccess() = toastHelper.showToast(getString(R.string.suggestions_sent))

    override fun onFail() = toastHelper.showToast(getString(R.string.suggestions_fail))

}