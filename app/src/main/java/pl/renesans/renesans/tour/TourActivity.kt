package pl.renesans.renesans.tour

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.activity_tour.*
import pl.renesans.renesans.R
import pl.renesans.renesans.SuggestionBottomSheetDialog
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.Photo
import pl.renesans.renesans.data.PhotoArticle
import pl.renesans.renesans.data.Tour
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.data.firebase.FirebaseContract
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.map.ClusterManagerRenderer
import pl.renesans.renesans.map.ClusterMarker
import pl.renesans.renesans.photo.PhotoActivity
import pl.renesans.renesans.settings.SettingsPresenterImpl
import pl.renesans.renesans.toast.ToastHelperImpl

class TourActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,
    ImageDaoContract.ImageDaoInterractor, TourContract.TourView, OnMapReadyCallback,
    FirebaseContract.FirebaseInterractor{

    private lateinit var tour: Tour
    private lateinit var presenter: TourContract.TourPresenter
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var tourAdapter: TourAdapter
    private val toastHelper = ToastHelperImpl(this)
    private var markers = mutableListOf<ClusterMarker>()
    private var currentPage = 0
    private var numberOfPages = 0

    private var googleMap: GoogleMap? = null
    private var clusterManager: ClusterManager<ClusterMarker>? = null
    private var clusterManagerRenderer: ClusterManagerRenderer? = null
    private var landscapeRotation = false
    private var portraitRotation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tour)
        setSupportActionBar(tourToolbar)
        setupMap()
        setupTheme()
        getDeviceRotation()
        tour = getTourObject()
        numberOfPages = tour.photosArticlesList!!.size
        presenter = TourPresenterImpl(applicationContext, this)
        tourAdapter = TourAdapter(this, tour)
        tourToolbar.title = "${tour.title}・Interaktywny szlak"
        tourPager.adapter = tourAdapter
        tourPager.addOnPageChangeListener(this)
        backBtn.setOnClickListener{ showPreviousPage() }
        nextBtn.setOnClickListener{ showNextPage() }
        presenter.onCreate()
        onPageSelected(0)
    }

    private fun setupMap(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupTheme(){
        sharedPrefs = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        val mapOpacity = sharedPrefs.getBoolean(SettingsPresenterImpl.MAP_OPACITY, true)
        if(!mapOpacity) shadowView.visibility = View.INVISIBLE
    }

    private fun getDeviceRotation(){
        landscapeRotation = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        portraitRotation = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        presenter.mapReady()
        setUiSettings()
        prepareManagers()
        presenter.addMarkers()
    }

    private fun setUiSettings(){
        googleMap?.uiSettings?.isMapToolbarEnabled = false
        googleMap?.uiSettings?.isZoomControlsEnabled = false
        googleMap?.uiSettings?.isCompassEnabled = false
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(applicationContext, R.raw.map_style))
    }

    private fun prepareManagers(){
        if(clusterManager == null) clusterManager = ClusterManager(this, googleMap)
        if(clusterManagerRenderer == null){
            clusterManagerRenderer = ClusterManagerRenderer(this, googleMap!!, clusterManager!!)
            clusterManagerRenderer?.prepareMarker()
            clusterManager?.renderer = clusterManagerRenderer
        }
    }

    override fun getTourObject(): Tour {
        return intent.getSerializableExtra(TOUR) as Tour
    }

    private fun showNextPage() {
        tourPager.currentItem = currentPage + 1
    }

    private fun showPreviousPage() {
        tourPager.currentItem = currentPage - 1
    }

    override fun onPageScrollStateChanged(state: Int) { }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

    override fun onPageSelected(position: Int) {
        checkTourLayout()
        tour.photosArticlesList!![position].paragraph?.subtitle =
            tour.photosArticlesList!![position].photo?.description
        currentPage = position
        tourProgress.progress = (((position + 2).toDouble() / (numberOfPages + 1).toDouble()) * 100).toInt()
        setBackBtnProperties(position)
        setNextBtnProperties(position)
        checkUserHasEndedTour(position)
        articlePhoto.setOnClickListener{ startPhotoViewActivity(position) }
        presenter.onPageSelected(position)
    }

    private val articleConverter = ArticleConverterImpl()

    private fun startPhotoViewActivity(pos: Int){
        val intent = Intent(applicationContext, PhotoActivity::class.java)
        val article = articleConverter.convertPhotoArticleToArticle(tour.photosArticlesList!![pos])
        val listOfPhotos = mutableListOf<Photo>()
        for(photoArticle in tour.photosArticlesList!!)
            if(photoArticle.objectId!=null)
                listOfPhotos.add(Photo(objectId = photoArticle.objectId + "_0",
                    description = photoArticle.photo?.description))
        article.listOfPhotos = listOfPhotos
        intent.putExtra(PhotoActivity.ARTICLE, article)
        intent.putExtra(PhotoActivity.POSITION, pos)
        startActivity(intent)
    }

    private fun checkUserHasEndedTour(position: Int){
        if(position == numberOfPages - 1) showAllTour()
    }

    private fun setBackBtnProperties(position: Int){
        if(position==0) backBtn.visibility = View.INVISIBLE
        else backBtn.visibility = View.VISIBLE
    }

    private fun checkTourLayout(){
        val params = mapConstraint.layoutParams as LinearLayout.LayoutParams
        if(articlePhoto.visibility == View.GONE){
            params.weight = 1f
            articlePhoto.visibility = View.VISIBLE
        }
    }

    private fun setNextBtnProperties(position: Int){
        if(position == numberOfPages-1){
            nextBtn.text = getString(R.string.end)
            nextBtn.setOnClickListener{ finish() }
        }else{
            nextBtn.text = getString(R.string.next)
            nextBtn.setOnClickListener{ showNextPage() }
        }
    }

    private fun showAllTour(){
        val params = mapConstraint.layoutParams as LinearLayout.LayoutParams
        if(portraitRotation) params.weight = 2f
        articlePhoto.visibility = View.GONE
        animateWholeTourCamera()
    }

    private fun animateWholeTourCamera(){
        if(markers.isEmpty()) return
        val builder = LatLngBounds.Builder()
        for (marker in markers) builder.include(marker.position)
        val bounds = builder.build()
        val cameraUpdate =
            if(landscapeRotation) CameraUpdateFactory.newLatLngBounds(bounds, 120)
            else CameraUpdateFactory.newLatLngBounds(bounds, 0)
        googleMap?.animateCamera(cameraUpdate)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        Glide.with(applicationContext).load(photoUri).placeholder(articlePhoto.drawable).into(articlePhoto)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        if(!photoBitmap.isRecycled)
            Glide.with(applicationContext).load(photoBitmap).placeholder(articlePhoto.drawable).into(articlePhoto)
    }

    override fun addClusterMarkerToMap(cluster: ClusterMarker) {
        markers.add(cluster)
        clusterManager?.addItem(cluster)
        clusterManager?.cluster()
    }

    override fun animateCamera(latLng: LatLng) {
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f), 1500, null)
    }

    override fun moveCamera(latLng: LatLng) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    override fun showSuggestionBottomSheet(article: Article) =
        SuggestionBottomSheetDialog().newInstance(article, 0)
            .show(supportFragmentManager, "Paragraph")

    companion object {
        const val TOUR = "tour"
    }

    override fun onSuccess() = toastHelper.showToast(getString(R.string.suggestions_sent))

    override fun onFail() = toastHelper.showToast(getString(R.string.suggestions_fail))
}
