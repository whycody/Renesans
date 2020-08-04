package pl.renesans.renesans.photo

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_photo.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Article

class PhotoActivity : AppCompatActivity(), PhotoInterractor, ViewPager.OnPageChangeListener {

    private var photoAdapter: PhotoAdapter? = null
    private var pagerView: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        setSupportActionBar(photoToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        photoToolbar.navigationIcon?.setColorFilter(ContextCompat.getColor(this,
            android.R.color.white), PorterDuff.Mode.SRC_ATOP)
        changeStatusBarColor()
        pagerView = photoPager
        photoAdapter = PhotoAdapter(applicationContext, getArticle(), this)
        photoPager.addOnPageChangeListener(this)
        photoPager.adapter = photoAdapter
        photoPager.offscreenPageLimit = 10
        photoPager.currentItem = getPosition()
        photoPager.setPageTransformer(true, ZoomOutPageTransformer())
        photoToolbar.title = getArticle().listOfPhotos!![getPosition()].description
    }

    override fun photoClicked() = showOrHideToolbar()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showOrHideToolbar(){
        if(photoAppBar.visibility == View.VISIBLE) photoAppBar.visibility = View.INVISIBLE
        else photoAppBar.visibility = View.VISIBLE
    }

    private fun changeStatusBarColor(){
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)
    }

    private fun getPosition() = intent.getIntExtra(POSITION, 0)

    private fun getArticle() = intent.getSerializableExtra(ARTICLE) as Article

    override fun onPageScrollStateChanged(state: Int) { }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

    override fun onPageSelected(position: Int) {
        photoToolbar.title = getArticle().listOfPhotos!![position].description
        photoAppBar.visibility = View.VISIBLE
    }

    companion object {
        const val ARTICLE = "article"
        const val POSITION = "position"
    }
}
