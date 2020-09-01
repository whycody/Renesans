package pl.renesans.renesans.photo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_photo.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.Photo
import pl.renesans.renesans.data.Source
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.utility.AlertDialogUtilityImpl
import pl.renesans.renesans.utility.ConnectionUtility
import pl.renesans.renesans.utility.ConnectionUtilityImpl

class PhotoActivity : AppCompatActivity(), PhotoInterractor, ViewPager.OnPageChangeListener,
    ImageDaoContract.ImageDaoDownloadInterractor, ImageDaoContract.ImageDaoInterractor {

    private var photoAdapter: PhotoAdapter? = null
    private var pagerView: ViewPager? = null
    private var photoSource: Source? = null
    private var photoDesc: String? = null
    private var currentPhoto: Photo? = null
    private val alertDialogUtility = AlertDialogUtilityImpl(this)
    private lateinit var imageDao: ImageDaoImpl
    private lateinit var connectionUtility: ConnectionUtility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        setSupportActionBar(photoToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        photoToolbar.navigationIcon?.setColorFilter(ContextCompat
            .getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP)
        changeStatusBarColor()
        imageDao = ImageDaoImpl(applicationContext, this, this)
        connectionUtility = ConnectionUtilityImpl(this)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(currentPhoto == null) currentPhoto = getPhoto()
        photoSource = currentPhoto?.source
        photoDesc = currentPhoto?.description
        addItemsToMenu(menu)
        return true
    }

    private fun addItemsToMenu(menu: Menu?) {
        if(imageDao.highQualityPhotoIsAvailable(currentPhoto?.objectId!!))
            addSharingPhotoMenuItems(menu)
        if(connectionUtility.isConnectionAvailable()) addSearchMenuItems(menu)
    }

    private fun addSharingPhotoMenuItems(menu: Menu?) {
        menu?.add(0, 0, 0, getString(R.string.save))
        menu?.add(0, 1, 0, getString(R.string.share))
    }

    private fun addSearchMenuItems(menu: Menu?) {
        if(photoSource!=null) menu?.add(0, 2, 0, getString(R.string.show_source))
        if(photoDesc!=null) menu?.add(0, 3, 0, getString(R.string.search_more_photos))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            0 -> checkDownloadPhotosPermissions()
            1 -> imageDao.getPhotoUri(currentPhoto?.objectId!!)
            2 -> openSource()
            3 -> searchMorePhotos()
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    private fun checkDownloadPhotosPermissions() {
        if(downloadPhotosPermissionIsGranted())
            imageDao.savePhotoToExternalStorage(currentPhoto?.objectId!!)
        else alertDialogUtility.getDownloadPhotosPermissionDialog().show()
    }

    private fun downloadPhotosPermissionIsGranted() = (ContextCompat.checkSelfPermission(applicationContext,
        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

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
        invalidateOptionsMenu()
        photoToolbar.title = getArticle().listOfPhotos!![position].description
        photoAppBar.visibility = View.VISIBLE
    }

    override fun downloadFailed() =
        Toast.makeText(applicationContext, getString(R.string.try_again_later), Toast.LENGTH_SHORT).show()

    override fun downloadSuccess() =
        Toast.makeText(applicationContext, getString(R.string.photo_download_successfully), Toast.LENGTH_SHORT).show()

    override fun photoExists() =
        Toast.makeText(applicationContext, getString(R.string.photo_is_already_downloaded), Toast.LENGTH_SHORT).show()

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) = sharePhoto(photoUri)

    private fun sharePhoto(photoUri: Uri){
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_TEXT, photoDesc)
        intent.putExtra(Intent.EXTRA_TITLE, photoDesc)
        intent.putExtra(Intent.EXTRA_STREAM, photoUri)
        intent.type = "image/*"
        startActivity(Intent.createChooser(intent, photoDesc))
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) { }

    companion object {
        const val ARTICLE = "article"
        const val POSITION = "position"
    }
}
