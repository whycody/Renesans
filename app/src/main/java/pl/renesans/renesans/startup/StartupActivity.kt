package pl.renesans.renesans.startup

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_startup.*
import pl.renesans.renesans.MainActivity
import pl.renesans.renesans.R
import pl.renesans.renesans.SplashActivity
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.download.DownloadActivity
import pl.renesans.renesans.permission.PermissionActivity

class StartupActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,
    Animation.AnimationListener {

    private var dots = mutableListOf<View>()
    private var currentPage = 0
    private var permissionGranted = false
    private var prefs: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private lateinit var realmDao: RealmContract.RealmDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
        changeStatusBarColor()
        prefs = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        editor = prefs!!.edit()
        val startupAdapter = StartupAdapter(this)
        permissionGranted = (ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        startupPager.adapter = startupAdapter
        startupPager.addOnPageChangeListener(this)
        backBtn.setOnClickListener{ showPreviousPage() }
        nextBtn.setOnClickListener{ showNextPage() }
        addDotsIndicator()
    }

    private fun showNextPage(){
        startupPager.currentItem = currentPage + 1
    }

    private fun showPreviousPage(){
        startupPager.currentItem = currentPage - 1
    }

    private fun changeStatusBarColor(){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
    }

    private fun addDotsIndicator(){
        for(i in 0.. 2){
            val view = getDefaultView()
            dots.add(view)
            dotsLayout.addView(view)
        }
        dots[0].background = getDrawable(R.drawable.sh_circle_primary)
    }

    private fun getDefaultView(): View {
        val view = View(this)
        val params = LinearLayout.LayoutParams(18, 18, 0.0f)
        params.setMargins(6, 1, 6, 0)
        view.layoutParams = params
        view.background = getDrawable(R.drawable.sh_circle_transp_primary)
        return view
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        dots[currentPage].background = getDrawable(R.drawable.sh_circle_transp_primary)
        dots[position].background = getDrawable(R.drawable.sh_circle_primary)
        currentPage = position
        setBackBtnProperties(position)
        setNextBtnProperties(position)
    }

    private fun setBackBtnProperties(position: Int){
        if(position==0) backBtn.visibility = View.GONE
        else backBtn.visibility = View.VISIBLE
    }

    private fun setNextBtnProperties(position: Int){
        if(position==dots.size-1){
            nextBtn.text = getString(R.string.end)
            nextBtn.setOnClickListener{ startNewActivity() }
        }else{
            nextBtn.text = getString(R.string.next)
            nextBtn.setOnClickListener{ showNextPage() }
        }
    }

    private fun startNewActivity() {
        editor?.putBoolean(SplashActivity.firstLogin, false)
        editor?.apply()
        realmDao = RealmDaoImpl(applicationContext)
        realmDao.onCreate()
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
        animation.setAnimationListener(this)
        relativeLayout.startAnimation(animation)
    }

    override fun onAnimationRepeat(p0: Animation?) {

    }

    override fun onAnimationEnd(p0: Animation?) {
        if(!permissionGranted) startActivity(Intent(this, PermissionActivity::class.java))
        else if(realmDao.realmDatabaseIsEmpty())
            startActivity(Intent(this, DownloadActivity::class.java))
        else startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(0, 0)
        finish()
    }

    override fun onAnimationStart(p0: Animation?) {

    }
}
