package pl.renesans.renesans.photo

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_photo.*
import pl.renesans.renesans.BuildConfig
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.Photo
import pl.renesans.renesans.data.Source
import pl.renesans.renesans.data.image.ImageDaoImpl
import java.io.File

class PhotoActivity : AppCompatActivity(), PhotoInterractor, ViewPager.OnPageChangeListener {

    private var photoAdapter: PhotoAdapter? = null
    private var pagerView: ViewPager? = null
    private var photoSource: Source? = null
    private var photoDesc: String? = null
    private var photoFile: File? = null
    private var currentPhoto: Photo? = null
    private lateinit var imageDao: ImageDaoImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        setSupportActionBar(photoToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        photoToolbar.navigationIcon?.setColorFilter(ContextCompat
            .getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP)
        changeStatusBarColor()
        imageDao = ImageDaoImpl(applicationContext)
        pagerView = photoPager
        photoAdapter = PhotoAdapter(applicationContext, getArticle(), this)
        photoPager.addOnPageChangeListener(this)
        photoPager.adapter = photoAdapter
        photoPager.offscreenPageLimit = 10
        photoPager.currentItem = getPosition()
        photoPager.setPageTransformer(true, ZoomOutPageTransformer())
        photoToolbar.title = getArticle().listOfPhotos!![getPosition()].description
    }

    override fun onResume() {
        super.onResume()
        if(photoDesc!=null) overridePendingTransition(0, 0)
    }

    override fun photoClicked() = showOrHideToolbar()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(currentPhoto == null) currentPhoto = getPhoto()
        photoFile = imageDao.getPhotoFile(currentPhoto?.objectId!!)
        photoSource = currentPhoto?.source
        photoDesc = currentPhoto?.description
        addItemsToMenu(menu)
        return true
    }

    private fun addItemsToMenu(menu: Menu?) {
        if(photoFile!=null)
            menu?.add(0, 0, 0, getString(R.string.share))
        if(photoSource!=null)
            menu?.add(0, 1, 0, getString(R.string.show_source))
        if(photoDesc!=null)
            menu?.add(0, 2, 0, getString(R.string.search_more_photos))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            0 -> sharePhoto(photoFile!!)
            1 -> openSource()
            2 -> searchMorePhotos()
        }
        return true
    }

    private fun sharePhoto(file: File){
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_TEXT, photoDesc)
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file))
        intent.type = "image/*"
        startActivity(Intent.createChooser(intent, photoDesc))
    }

    private fun openSource(){
        val url = photoSource?.url
        if(url!=null) startUrlActivity(url)
    }

    private fun searchMorePhotos(){
        val url = "https://www.google.com/search?q=$photoDesc&tbm=isch"
        startUrlActivity(url)
    }

    private fun startUrlActivity(url: String) {
        val uriUrl = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
        overridePendingTransition(0, 0)
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

    private fun getPhoto() = getArticle().listOfPhotos!![getPosition()]

    private fun getPosition() = intent.getIntExtra(POSITION, 0)

    private fun getArticle() = intent.getSerializableExtra(ARTICLE) as Article

    override fun onPageScrollStateChanged(state: Int) { }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

    override fun onPageSelected(position: Int) {
        currentPhoto = getArticle().listOfPhotos!![position]
        photoSource = currentPhoto?.source
        photoDesc = currentPhoto?.description
        invalidateOptionsMenu()
        photoToolbar.title = getArticle().listOfPhotos!![position].description
        photoAppBar.visibility = View.VISIBLE
    }

    companion object {
        const val ARTICLE = "article"
        const val POSITION = "position"
    }
}
